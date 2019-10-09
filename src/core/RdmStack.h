#ifndef RDMSTACK_H
#define RDMSTACK_H

#include <string.h>
#include <assert.h>

#include <thread>
#include <atomic>
#include <mutex>
#include <map>
#include <vector>

#include "HPNL/BufMgr.h"
#include "core/Stack.h"
class RdmConnection;
class RdmStack : public Stack {
  public:
    RdmStack(int, int, int, int, bool, const char*);
    ~RdmStack();
    virtual int init() override;
    virtual void* bind(const char*, int, BufMgr*, BufMgr*) override;
    RdmConnection* get_con(const char*, int, const char*, int, uint64_t, uint64_t, int, int, BufMgr*, BufMgr*);
    void setup_endpoint(const char*, int);
    fid_fabric* get_fabric();
    fid_cq** get_cqs();

    RdmConnection* get_connection(long id);
    void reap(long id);

  private:
    fi_info *info;
    fi_info *connect_info;
    fid_fabric *fabric;
    fid_domain *domain;
    fid_cq **cqs;
    fid_av *av;
    fid_ep *ep;
    fid_ep **tx;
    fid_ep **rx;

    char *local_name;
    size_t local_name_len;
    fi_addr_t local_provider_addr;

    int buffer_num;
    int recv_buffer_num;
    int ctx_num;
    int endpoint_num;
    bool is_server;

    std::map<long, RdmConnection*> conMap;
    std::mutex conMtx;
    std::atomic_long id_generator;

    std::mutex mtx;
    bool is_setup;

    RdmConnection *server_con;

    const char* prov_name;
};

#endif
