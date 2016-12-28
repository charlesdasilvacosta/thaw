import getopt
import json
import ssl
import sys
import urllib.request

# -h -> help
# -p -> port of server
# -a -> ip adress os server
# -c -> channel name
# -m -> message limit

if __name__ == "__main__":
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

ssl._create_default_https_context = ssl._create_unverified_context

resultRequest = urllib.request.urlopen('https://localhost:4443/users')

# json.dumps(result.read())

result = str(resultRequest.read()).replace('\\n','')
result = result.replace('b','')

print (json.dumps(result))