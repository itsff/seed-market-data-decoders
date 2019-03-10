#include <seedcx/market_data.hpp>
#include <seedcx/incremental_update_decoder.hpp>
#include "printer.hpp"

#include <iostream>
#include <atomic>
#include <csignal>
#include <boost/asio.hpp>
#include <boost/program_options.hpp>


const size_t MAX_UDP_LEN = 65'535 - 8 - 20; //65,535 − 8 byte UDP header − 20 byte IP header

namespace po   = ::boost::program_options;
namespace asio = ::boost::asio;

static asio::io_service io_service;
static char             receive_buffer[MAX_UDP_LEN] = { 0 };
static bool             print_heartbeats { false };

static std::unique_ptr<seedcx::sniffer::printer> printer;

static void
signal_handler (const boost::system::error_code&,
                int signal_number);

static void
start_receive (asio::ip::udp::socket   &sock,
               asio::ip::udp::endpoint &endpoint);

static void
show_help (const po::options_description &options);

static asio::ip::udp::socket
make_multicast_socket (asio::io_service              &ios,
                       const asio::ip::udp::endpoint &endpoint,
                       const asio::ip::address       &iface);


int
main (int argc, char **argv)
{
    po::options_description options("Multicast Sniffer Tool");

    options.add_options()
        ("help,h", "Display this help message.")
        ("mcast,m", po::value<std::string>()->required(), "Multicast IP address.")
        ("port,p",  po::value<unsigned short>()->required(), "Multicast port.")
        ("interface,i", po::value<std::string>(), "IP of network interface.")
        ("heartbeats,b", po::bool_switch(&print_heartbeats)->default_value(false), "Include heartbeats.")
        ;

    try
    {
        po::variables_map vm;
        po::store(po::command_line_parser(argc, argv).options(options).run(), vm);

        if (vm.count("help"))
        {
            show_help(options);
            return EXIT_SUCCESS;
        }
        vm.notify();

        using namespace seedcx;

        printer = std::make_unique<sniffer::printer>(std::cout, print_heartbeats);

        asio::ip::address iface_ip;
        asio::ip::address mcast_ip = asio::ip::address::from_string(vm["mcast"].as<std::string>());
        unsigned short    port     = vm["port"].as<unsigned short>();

        if (vm.count("interface"))
        {
            iface_ip = asio::ip::address::from_string(vm["interface"].as<std::string>());
        }

        if (!mcast_ip.is_multicast() || !mcast_ip.is_v4())
        {
            throw std::runtime_error("Specify a valid IPv4 multicast address");
        }

        asio::ip::udp::endpoint endpoint { mcast_ip, port };
        asio::ip::udp::socket sock = make_multicast_socket(io_service, endpoint, iface_ip);

        // Construct a signal set registered for process termination.
        boost::asio::signal_set signals(io_service, SIGINT);
        signals.async_wait(signal_handler);

        // Start receiving UDP data
        std::cout
            << "Listening to multicast data on "
            << endpoint
            << " via net interface "
            << iface_ip
            << std::endl;
        start_receive(sock, endpoint);

        io_service.run();
    }
    catch (const po::error &po_error)
    {
        std::cerr
                << std::endl
                << "Problem: "
                << po_error.what()
                << std::endl
                << std::endl;
        show_help(options);

        return EXIT_FAILURE;
    }
    catch (const std::exception &ex)
    {
        std::cerr << "ERROR! " << ex.what() << std::endl << std::endl;
        return EXIT_FAILURE;
    }

    std::cout << "done" << std::endl;
    return EXIT_SUCCESS;
}

static void
signal_handler (const boost::system::error_code&, int signal_number)
{
    std::cout << "Received signal: " << signal_number << std::endl;
    io_service.stop();
}

static void
start_receive (asio::ip::udp::socket   &sock,
               asio::ip::udp::endpoint &endpoint)
{
    sock.async_receive_from(
            asio::buffer(receive_buffer),
            endpoint,
            [&](const boost::system::error_code &ec, std::size_t)
            {
                if (!ec)
                {
                    // Decode received data
                    seedcx::decode_incremental_update(receive_buffer, *printer);

                    start_receive(sock, endpoint);
                }
            });
}

static void
show_help (const po::options_description &options)
{
    std::cout << options << std::endl << std::endl;
}


static asio::ip::udp::socket
make_multicast_socket (asio::io_service &io_service,
                       const asio::ip::udp::endpoint &endpoint,
                       const asio::ip::address &iface)
{
    asio::ip::udp::socket sock(io_service);
    sock.open(endpoint.protocol());
    sock.set_option(asio::socket_base::reuse_address(true));
    sock.bind(endpoint);

    // Let's up the buffer size
    boost::system::error_code ec;
    sock.set_option(asio::socket_base::receive_buffer_size(16 * 1024 * 1024), ec);
    if (ec)
    {
        std::cerr << "Problem setting receive buffer size: " << ec.message() << std::endl;
    }

    // Join multicast group on desired interface
    if (endpoint.address().is_v4() && iface.is_v4())
    {
        sock.set_option(asio::ip::multicast::join_group(endpoint.address().to_v4(),
                                                        iface.to_v4()));
    }
    else
    {
        throw std::runtime_error("TODO: Implement IPv6 multicast support");
    }

    return sock;
}
