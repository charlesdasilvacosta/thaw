import urllib.request
import ssl
import json

# data_put = b'\"test_channel\"/\"1\"'

# post_request = urllib.request.Request('http://localhost:8080/channel',data=data_put,method='PUT')
# result = urllib.request.urlopen(post_request)


ssl._create_default_https_context = ssl._create_unverified_context

result = urllib.request.urlopen('https://localhost:4443/users')

json.dumps(result.read())

print (result.read())
