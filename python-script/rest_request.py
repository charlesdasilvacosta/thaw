import getopt
import sys

import requests
from requests.packages.urllib3.exceptions import InsecureRequestWarning

# -h -> help
# -p -> port of server
# -a -> ip adress os server
# -c -> channel name
# -m -> message limit

def getUserName(id, ):
    users_request = requests.get('https://localhost:4443/users', verify=False)

    for user in users_request.json():
        if user.get('id') == id:
            return user.get('name')
    raise ValueError('Unknown user id : ' + str(id))

if __name__ == "__main__":
    channelid = -1
    ipadress = ''
    port = ''
    channelname = ''
    messagelimit = 10

    try:
        opts, args = getopt.getopt(sys.argv[1:], "hp:a:c:m::")
    except getopt.GetoptError:
        print('rest_request.py -p <port> -a <ip adress> -c <channel name> -m <message limit, default at 10>')
        sys.exist(2)
    for opt, arg in opts:
        if opt == '-h':
            print('rest_request.py -p <port> -a <ip adress> -c <channel name> -m <message limit, default at 10>')
            sys.exit();
        elif opt == '-p':
            port = arg
        elif opt == '-a':
            ipadress = arg
        elif opt == '-c':
            channelname = arg
        elif opt == '-m':
            messagelimit = int(arg)

requests.packages.urllib3.disable_warnings(InsecureRequestWarning)

req_channelid = requests.get('https://localhost:4443/channels', verify=False)

for channel in req_channelid.json():
    if channel.get('name') == channelname:
        channelid = channel.get('id_channel')

if channelid == -1:
    raise ValueError('Channel Name doesn\'t exist : ' + channelname)


req_message = requests.get('https://localhost:4443/message/'+str(channelid), verify=False)

for message in req_message.json():
    print('\nAuthor: ' + str(getUserName(message.get("author_id"))))
    print('Date: ' + message.get('post_date'))
    print('Massage: ' + message.get('message'))