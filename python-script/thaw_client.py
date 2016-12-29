import getopt
import sys

import requests
from requests.packages.urllib3.exceptions import InsecureRequestWarning


class Options:
    def __init__(self, argv):
        self.__ipadress = ''
        self.__port = ''
        self.__channelname = ''
        self.__messagelimit = 10

        self.__parseopt(argv)

    def __parseopt(self, argv):
        try:
            opts, args = getopt.getopt(argv, "hp:a:c:m::")
        except getopt.GetoptError:
            print('thaw_client.py -p <port> -a <ip adress> -c <channel name> -m <message limit, default at 10>')
            raise ValueError('Invalid option(s)')
        for opt, arg in opts:
            if opt == '-h':
                print('thaw_client.py -p <port> -a <ip adress> -c <channel name> -m <message limit, default at 10>')
                sys.exit();
            elif opt == '-p':
                self.__port = arg
            elif opt == '-a':
                self.__ipadress = arg
            elif opt == '-c':
                self.__channelname = arg
            elif opt == '-m':
                self.__messagelimit = int(arg)

    def getipadress(self):
        return self.__ipadress

    def getport(self):
        return self.__port

    def getchannelname(self):
        return self.__channelname

    def getmessagelimit(self):
        return self.__messagelimit


class Client:
    def __init__(self, ipadress, port):
        self.__ipadress = ipadress
        self.__port = port

    def url(self, requestbody):
        self.__response = requests.get('https://' + self.__ipadress + ':' + self.__port + '/' + requestbody,
                                       verify=False)

    def json(self):
        return self.__response.json()


def getusername(id, options):
    users_client = Client(options.getipadress(),options.getport())
    users_client.url('users')

    for user in users_client.json():
        if user.get('id') == id:
            return user.get('name')
    raise ValueError('Unknown user id: ' + str(id))

if __name__ == '__main__':
    requests.packages.urllib3.disable_warnings(InsecureRequestWarning)
    options = Options(sys.argv[1:])

    messagelimit = options.getmessagelimit()

    channelid_client = Client(options.getipadress(), options.getport())
    channelid_client.url('channels')

    channelid = -1

    for json_channel in channelid_client.json():
        if json_channel.get('name') == options.getchannelname():
            channelid = json_channel.get('id_channel')

    if channelid == -1:
        raise ValueError('Channel name doesn\'t exist: ' + options.getchannelname())

    message_client = Client(options.getipadress(), options.getport())
    message_client.url('message/'+str(channelid))



    for message in reversed(message_client.json()):
        if messagelimit == 0:
            break
        messagelimit -= 1
        print('\nAuthor: ' + getusername(message.get('author_id'),options))
        print('Date: ' + message.get('post_date'))
        print('Message: ' + message.get('message'))
