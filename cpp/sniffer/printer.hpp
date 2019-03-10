#pragma once

#include <seedcx/incremental_update_decoder.hpp>
#include <ostream>
#include <boost/chrono/time_point.hpp>
#include <boost/chrono/io/time_point_io.hpp>
#include <boost/chrono/chrono.hpp>


namespace seedcx {
namespace sniffer {

//! Example class which prints multicast packets.
//! It uses the CRTP pattern for callbacks.
class printer : public incremental_update_handler_base<printer>
{
    std::ostream &_os;
    bool          _print_heartbeats;

public:
    printer (std::ostream &os, bool print_heartbeats)
        : _os(os)
        , _print_heartbeats(print_heartbeats)
    {
    }

private:
    template <typename msg_t>
    void log (const instrument_id_t  instrument_id,
              const sequence_num_t   sequence_num,
              const packet_header   &pkt_hdr,
              const msg_t           &msg)
    {
        _os
            << boost::chrono::time_fmt(boost::chrono::timezone::local)
            << boost::chrono::system_clock::now()
            << "|inst:"
            << instrument_id
            << "|seq:"
            << sequence_num
            << "|sent:"
            << pkt_hdr.sending_time
            << "|"
            << msg;
    }

public:
    void
    handle_heartbeat (
        const instrument_id_t  instrument_id,
        const sequence_num_t   sequence_num,
        const packet_header   &pkt_hdr)
    {
        if (_print_heartbeats)
        {
            log(instrument_id, sequence_num, pkt_hdr, "heartbeat");
        }
    }

    void
    handle_packet_header (
        const instrument_id_t  instrument_id,
        const sequence_num_t   sequence_num,
        const packet_header   &pkt_hdr)
    {
        // nada
    }

    void
    handle_message_header (
        const instrument_id_t  instrument_id,
        const sequence_num_t   sequence_num,
        const packet_header   &pkt_hdr,
        const message_header  &msg_hdr)
    {
        // nada
    }

    void
    handle_unknown_message (
        const instrument_id_t  instrument_id,
        const sequence_num_t   sequence_num,
        const packet_header   &pkt_hdr,
        const message_header  &msg_hdr,
        const char            *message)
    {
        log(instrument_id, sequence_num, pkt_hdr, "unknown msg:" + std::to_string(msg_hdr.type));
    }

    void
    handle_clear_book (
        const instrument_id_t         instrument_id,
        const sequence_num_t          sequence_num,
        const packet_header          &pkt_hdr,
        const message_header         &msg_hdr,
        const clear_book &message)
    {
        log(instrument_id, sequence_num, pkt_hdr, message);
    }

    void
    handle_add_order (
        const instrument_id_t         instrument_id,
        const sequence_num_t          sequence_num,
        const packet_header          &pkt_hdr,
        const message_header         &msg_hdr,
        const add_order &message)
    {
        log(instrument_id, sequence_num, pkt_hdr, message);
    }

    void
    handle_replace_order (
        const instrument_id_t         instrument_id,
        const sequence_num_t          sequence_num,
        const packet_header          &pkt_hdr,
        const message_header         &msg_hdr,
        const replace_order &message)
    {
        log(instrument_id, sequence_num, pkt_hdr, message);
    }

    void
    handle_delete_order (
        const instrument_id_t         instrument_id,
        const sequence_num_t          sequence_num,
        const packet_header          &pkt_hdr,
        const message_header         &msg_hdr,
        const delete_order &message)
    {
        log(instrument_id, sequence_num, pkt_hdr, message);
    }

    void
    handle_trading_status (
        const instrument_id_t         instrument_id,
        const sequence_num_t          sequence_num,
        const packet_header          &pkt_hdr,
        const message_header         &msg_hdr,
        const trading_status &message)
    {
        log(instrument_id, sequence_num, pkt_hdr, message);
    }

    void
    handle_trade (
        const instrument_id_t         instrument_id,
        const sequence_num_t          sequence_num,
        const packet_header          &pkt_hdr,
        const message_header         &msg_hdr,
        const trade &message)
    {
        log(instrument_id, sequence_num, pkt_hdr, message);
    }

    void
    handle_trade_break (
        const instrument_id_t         instrument_id,
        const sequence_num_t          sequence_num,
        const packet_header          &pkt_hdr,
        const message_header         &msg_hdr,
        const trade_break &message)
    {
        log(instrument_id, sequence_num, pkt_hdr, message);
    }

    void
    handle_session_end (
        const instrument_id_t         instrument_id,
        const sequence_num_t          sequence_num,
        const packet_header          &pkt_hdr,
        const message_header         &msg_hdr,
        const session_end &message)
    {
        log(instrument_id, sequence_num, pkt_hdr, message);
    }
};


} // namespace sniffer
} // namespace seedcx
