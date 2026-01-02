# qemu-java-process

## QemuLauncher.java (main)
Launches a QEMU VM on Apple Silicon. The qemu_example.txt has been converted to run using the Java ProcessBuilder. 
It has also been invokd with the -qmp flag to expose a unix socket. QMP allows JSON requests to QEMU.

## QmpShutdownClient.java (main)
An example issuing QMP JSON requests via Unix Socket connection - includes the shutdown to force stop the VM. 