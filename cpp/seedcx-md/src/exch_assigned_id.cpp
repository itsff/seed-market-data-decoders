#include "seedcx/exch_assigned_id.hpp"

#include <vector>
#include <string>
#include <cassert>


static std::string
EncodeBase58 (const unsigned char* pbegin, const unsigned char* pend)
{
    // This function adapted from:
    // https://dev.visucore.com/bitcoin/doxygen/base58_8cpp_source.html
    //
    // Copyright (c) 2014-2018 The Bitcoin Core developers
    // Distributed under the MIT software license, see
    // http://www.opensource.org/licenses/mit-license.php.
    //

    static const char* pszBase58 = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    // Skip & count leading zeroes.
    int zeroes = 0;
    int length = 0;
    while (pbegin != pend && *pbegin == 0) {
        pbegin++;
        zeroes++;
    }

    // Allocate enough space in big-endian base58 representation.
    int size = (pend - pbegin) * 138 / 100 + 1; // log(256) / log(58), rounded up.
    std::vector<unsigned char> b58(size);
    // Process the bytes.
    while (pbegin != pend)
    {
        int carry = *pbegin;
        int i = 0;
        // Apply "b58 = b58 * 256 + ch".
        for (std::vector<unsigned char>::reverse_iterator it = b58.rbegin();
             (carry != 0 || i < length) && (it != b58.rend());
             ++it, ++i)
        {
            carry += 256 * (*it);
            *it = carry % 58;
            carry /= 58;
        }

        assert(carry == 0);
        length = i;
        pbegin++;
    }
    // Skip leading zeroes in base58 result.
    std::vector<unsigned char>::iterator it = b58.begin() + (size - length);
    while (it != b58.end() && *it == 0) { it++; }
    // Translate the result into a string.
    std::string str;
    str.reserve(zeroes + (b58.end() - it));
    str.assign(zeroes, '1');
    while (it != b58.end()) { str += pszBase58[*(it++)]; }
    return str;
}


namespace seedcx {

std::ostream &
operator<< (std::ostream &os, const exch_assigned_id &id)
{
    uint64_t lo = *reinterpret_cast<const uint64_t*>(&id.data[0]);
    uint64_t hi = *reinterpret_cast<const uint64_t*>(&id.data[8]);

    if (hi == 0)
    {
        os << lo;
    }
    else
    {
        os << EncodeBase58(id.data, id.data + sizeof(id.data));
    }

    return os;
}

} // namespace seedcx
