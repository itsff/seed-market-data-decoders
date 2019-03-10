#pragma once
#include <cstdint>
#include <vector>
#include <cstring>
#include <ostream>

namespace seedcx {


#pragma pack(push)
struct exch_assigned_id
{
    uint8_t data[16];

    int
    compare (const exch_assigned_id &other) const
    {
        return memcmp(data, other.data, sizeof(data));
    }

    friend bool
    operator== (const exch_assigned_id &lhs, const exch_assigned_id &rhs)
    {
        return lhs.compare(rhs) == 0;
    }

    friend bool
    operator!= (const exch_assigned_id &lhs, const exch_assigned_id &rhs)
    {
        return lhs.compare(rhs) != 0;
    }

    friend bool
    operator<  (const exch_assigned_id &lhs, const exch_assigned_id &rhs)
    {
        return lhs.compare(rhs) <  0;
    }

    friend std::ostream &
    operator<< (std::ostream &os, const exch_assigned_id &id);
};
#pragma pack(pop)

static_assert(sizeof(exch_assigned_id) == 16, "Size must be 128-bit");

} // namespace seed
