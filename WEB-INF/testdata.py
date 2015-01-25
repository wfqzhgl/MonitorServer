#-*-coding:utf8-*-
import copy
import random
import json

d = {"ver":"v1.0","status":"ok","comment":"get event list ok!","begintime":"1407397473","data":[]}
sub_data = {"type":"3","msglist":[],"datalist":[]}
sub_msglist = '{"msg_title":"2014-12-03 13:13:13 $sip($sc)->$dip($dc) $type","msg_data":"$type"}'
sub_datalist = '{"sip":"$sip","sport":"50677","dip":"$dip","dport":"80"}'
type_list = [u"WEB漏洞攻击",u"协议异常",u"后门传输",u"非法外联",u"恶意代码",u"加密传输",u"蠕虫攻击"]
sc_list = [u"中国",u"美国",u"日本",u"英国",u"印度",u"法国",u"澳大利亚",u"巴西",u"台湾",u"新加坡",u"德国",u"俄罗斯"]
dc_list = [u"中国",u"美国",u"日本",u"俄罗斯"]
ip_dic = {u"中国":"168.160.249.",u"美国":"5.152.184.",u"日本":"14.8.12.",u"英国":"25.244.90.",u"印度":"27.34.240.",u"法国":"31.7.248.",u"澳大利亚":"42.62.192.",u"巴西":"143.54.0.",u"台湾":"163.13.0.",u"新加坡":"164.78.0.",u"德国":"164.138.192.",u"俄罗斯":"164.215.64."}
for i in xrange(5):
    f=open("sanlie%s.json" % i,"wb")
    line = copy.deepcopy(d)
    data =  copy.deepcopy(sub_data)
    for i in xrange(50):
        
        sc = random.choice(sc_list)
        dc = random.choice(dc_list)
        tp = random.choice(type_list)
        sip = ip_dic[sc]+u"%s" % random.randint(1,225)
        dip = ip_dic[dc]+u"%s" % random.randint(1,225)
        data["msglist"].append(json.loads(sub_msglist.replace("$sip",sip).replace("$dip",dip).replace("$sc",sc).replace("$dc",dc).replace("$type",tp)))
        data["datalist"].append(json.loads(sub_datalist.replace("$sip",sip).replace("$dip",dip)))
    line["data"].append(data)
    f.write(json.dumps(line))
    f.close()
        
    
