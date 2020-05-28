@snap[north]
### Debugging Greengrass Lambda functions like a boss
@snapend

@snap[south span-25]
![AWS IoT Greengrass](assets/svg/AWS-IoT-Greengrass.svg)
@snapend

---

@snap[north]
# Disclaimer!
@snapend

@ul[list-fade-fragments]
- Python and NodeJS are not my strong suits
- Please recommend improvements!
@ulend

---

## The challenge

+++

#### When your code is working it is beautifully simple

@code[python zoom-20](debugging/python-function-without-debug-statements.py)

+++
#### When your code isn't working it gets ugly

@code[python](debugging/python-function-with-debug-statements.py)

@snap[south span-100]
@[2, zoom-15](Overly verbose messages)
@[3,9, zoom-15](Duplicated blocks)
@[15, zoom-15](And sometimes the debug output fails)
@snapend

---

#### We have debuggers, let's use them!

![IntellJ][width=300, height=300](https://resources.jetbrains.com/storage/products/intellij-idea/img/meta/intellij-idea_logo_300x300.png)
![PyCharm][width=300, height=300](https://resources.jetbrains.com/storage/products/pycharm/img/meta/pycharm_logo_300x300.png)
![Chrome][width=300, height=300](https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Google_Chrome_icon_%28September_2014%29.svg/600px-Google_Chrome_icon_%28September_2014%29.svg.png)

---

#### But let's not go crazy, start small, and build up to that

+++

@snap[north]
#### Evolution of debugging
@snapend

![Evolution](https://www.popsci.com/resizer/5m5elGSAY-i4NW7NUbT1adc4-qg=/1034x519/arc-anglerfish-arc2-prod-bonnier.s3.amazonaws.com/public/6GVVAULRRPDTLXBASY7FSCENVQ.jpg)

+++

@snap[north]
#### Evolution of debugging
@snapend

@ul[list-fade-fragments]
- Print and tail -F via SSH
- CloudWatch logs in the console
- CloudWatch logs on the command-line with GGP
- Hooking up a real debugger
- Debugging and iterating with GGP
@ulend

+++

@snap[north span-100]
#### Print and tail -F via SSH
@snapend

@ul[list-fade-fragments]
- Pro: Works out of the box
- Pro: Works well for small functions
- Con: When functions get large filtering log messages can be complex
- Con: Requires remote access to the device (SSH, SSM, etc.)
- Con: The way tail separates output from multiple files can be an issue
@ulend

+++

@snap[north span-100]
#### CloudWatch logs in the console
@snapend

@ul[list-fade-fragments]
- Pro: Can access logs remotely and filter/search them
- Con: Requires familiarity with CloudWatch logging
- Con: Only one log stream per browser window
@ulend

+++

@snap[north span-100]
#### CloudWatch logs on the command-line with GGP
@snapend

@ul[list-fade-fragments]
- Pro: Can access logs remotely and filter/search them with CLI tools (grep, etc)
- Con: Requires additional tools (GGP)
- Con: grep style filtering can easily miss messages if you're not careful
@ulend

+++

@snap[north span-100]
#### Hooking up a real debugger
@snapend

@ul[list-fade-fragments]
- Pro: Watches, breakpoints, stepping, hotswapping data
- Pro: A general sense of pride that you've accomplished something insane
- Con: Significant setup required
- Con: Initial debugging of the debug setup can drive you mad
@ulend

+++

@snap[north span-100]
#### Debugging and iterating with GGP
@snapend

@ul[list-fade-fragments]
- Pro: Less than 1 minute to deploy new code
- Con: Workflow is somewhat restricted to the GGP model while iterating
@ulend

---

#### Print and tail -F via SSH

![Print and tail -F via SSH](https://www.youtube.com/embed/r7UJDsiNZ9I)

---

#### CloudWatch logs in the console

![CloudWatch logs in the console](https://www.youtube.com/embed/jLjj6JTYmiU)

---

#### CloudWatch logs on the command-line with GGP

![CloudWatch logs on the command-line with GGP](https://www.youtube.com/embed/Z6-nEKtvIIw)

+++

Old version:

![GGP CloudWatch support](https://www.youtube.com/embed/v_rE-qOzKvU)

---

#### Using a debugger

+++

@snap[north span-100]
#### Requirements
@snapend

@ul[list-fade-fragments]
- User must not have to modify their code to enable or disable debugging
- Debugging must not interfere with normal operation
@ulend

+++

@snap[north span-100]
#### What does Greengrass do when it starts a function?
@snapend

@ul[list-fade-fragments]
- Determines what runtime is needed
- 🤩 Looks for the runtime on the path 🤩
- Starts the runtime with your code
@ulend

+++

#### What if the runtime was not really the runtime?

+++

@snap[north span-100]
### 🤷‍♂️Greengrass does not care 🤷
@snapend

@snap[midpoint span-40]
@box[bg-gold text-white rounded box-padding](Let's get weird)
@snapend

+++

@snap[north span-100]
# The plan
@snapend

@ul[list-fade-fragments]
- Replace the runtime executable with a shell script
- Add environment variables to control the debug mode
@ulend

+++

#### General debugger flow

@uml[](debugging/general-debugger-flow.puml)

+++

@snap[north span-100]
#### Simplified NodeJS wrapper
@snapend

```bash
#!/usr/bin/env /bin/bash


# Is there a debug port? If not, generate a random one.
[[ -z "$DEBUG_PORT" ]] && DEBUG_PORT=$(((RANDOM%1000+9000)))

# Is debugging enabled? If so, create the NodeJS `--inspect` option.
[[ "$DEBUG" == "true" ]] && DEBUG_STRING="--inspect=$DEBUG_PORT"

# Run the NodeJS runtime with the optional debug string
#   followed by all of the original options from Greengrass
eval "$NODEJS $DEBUG_STRING $*"
```

+++

@snap[north span-100]
#### Simplified Java wrapper
@snapend

```bash
#!/usr/bin/env /bin/bash


# Is there a debug port? If not, generate a random one.
[[ -z "$DEBUG_PORT" ]] && DEBUG_PORT=$(((RANDOM%1000+9000)))

# Should we suspend execution when the function starts?
#   If not, set DEBUG_SUSPEND explicity to no.
[[ -z "$DEBUG_SUSPEND" ]] && DEBUG_SUSPEND=n

# Is debugging enabled? If so, create the Java `-Xdebug`
#   and `Xrunjdwp` options.
[[ "$DEBUG" == "true" ]] && DEBUG_STRING="-Xdebug -Xrunjdwp:"...
..."transport=dt_socket,address=localhost:$DEBUG_PORT,server=y,"...
..."suspend=$DEBUG_SUSPEND"

eval "$JAVA $DEBUG_STRING $*"
```

+++

#### Python turned out to be the hardest

+++

#### Python debugger injection flow, 1 of 2

@uml[](debugging/python-debugger-injection-flow-1.puml)

+++

#### Python debugger injection flow, 2 of 2

@uml[](debugging/python-debugger-injection-flow-2.puml)

+++

@snap[north span-100]
#### Python, bootstrapping code page 1 of 3
@snapend

```bash
if [ "$DEBUG" == "true" ]; then
  # Make sure we have unbuffered I/O (-u)
  cat <<EOF | eval "$PYTHON -u - $*"
import sys
import os

serversocket = None

def wait_for_connections():
    if serversocket is None:
        print ("No server socket present, debugging disabled")

    while 1:
        (clientsocket, address) = serversocket.accept()
        clientsocket.send(str.encode("Enter a port number: "))
        data = clientsocket.recv(5)
        outbound_port_string = data.decode("utf-8")
        clientsocket.send(str.encode("Attempting to connect to debugger on 127.0.0.1, port " + outbound_port_string))
        clientsocket.close()
        pydevd_pycharm.settrace('127.0.0.1', port=int(outbound_port_string), stdoutToServer=True, stderrToServer=True)
```

+++

@snap[north span-100]
#### Python, bootstrapping code page 2 of 3
@snapend
```python
try:
    import _thread
    import socket
    import pydevd_pycharm
    print ("pydevd_pycharm library imported, debugging enabled, connect on localhost port $DEBUG_PORT, enter a port number, and it will call back on localhost on that port")
    serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serversocket.bind(('127.0.0.1', $DEBUG_PORT))
    serversocket.listen(1)
    _thread.start_new_thread(wait_for_connections, ())

except ImportError:
    print ('pydevd_pycharm library missing, debugging disabled')
```

+++

@snap[north span-100]
#### Python, bootstrapping code page 3 of 3
@snapend
```python
# Remove the dash argument
sys.argv.pop(0)

# Find first .py argument to get the Lambda runtime
for arg in sys.argv:
    if arg.endswith('.py'):
        # Find the directory that the Lambda runtime is in
        import_directory = os.path.dirname(arg)
        # Add the directory to the system path
        sys.path.insert(0, os.path.abspath(import_directory));
        # Find the name of the Lambda runtime and remove the suffix
        import_name = os.path.basename(arg).replace('.py', '')
        # Import the Lambda runtime
        runtime = __import__(import_name)
        runtime.main()

        break
```

---

#### Debug videos

+++

@snap[north span-100]
#### Python debugging
@snapend

![Python debugging](https://www.youtube.com/embed/Zgwskficyg4)

+++

@snap[north span-100]
#### NodeJS debugging
@snapend

![NodeJS debugging](https://www.youtube.com/embed/Sn6l087zB9U)

+++

@snap[north span-100]
#### Java debugging
@snapend

![Java debugging](https://www.youtube.com/embed/XBiIMHhjwvA)

---
