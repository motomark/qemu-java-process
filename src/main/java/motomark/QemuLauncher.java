package motomark;

import java.nio.file.Path;
import java.util.List;

public class QemuLauncher {

    public static void main(String[] args) throws Exception {

        Path efiCode = Path.of("/opt/homebrew/share/qemu/edk2-aarch64-code.fd");
        Path efiVars = Path.of("/Users/markhawkins/git/qemu-apple-arm/edk2-arm-vars.fd");
        Path disk = Path.of("/Users/markhawkins/git/qemu-apple-arm/ubuntu-arm-template.qcow2");
        Path seed = Path.of("/Users/markhawkins/git/qemu-apple-arm/seed.iso");

        int cpus = 4;
        int memoryMb = 4096;
        int sshPort = 2222;

        List<String> command = List.of(
            "qemu-system-aarch64",
            "-qmp", 
            "unix:/tmp/qmp-sock,server,nowait",
            "-machine", "virt,accel=hvf",
            "-cpu", "host",
            "-smp", String.valueOf(cpus),
            "-m", String.valueOf(memoryMb),
            "-drive", "if=pflash,format=raw,readonly=on,file=" + efiCode,
            "-drive", "if=pflash,format=raw,file=" + efiVars,
            "-drive", "if=virtio,file=" + disk + ",format=qcow2",
            "-drive", "if=virtio,file=" + seed + ",format=raw",
            "-netdev", "user,id=net0,hostfwd=tcp::" + sshPort + "-:22",
            "-device", "virtio-net-pci,netdev=net0",
            "-nographic"
        );

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO(); // forward QEMU output to Java console

        System.out.println("Launching QEMU VM...");
        Process process = pb.start();

        int exitCode = process.waitFor();
        System.out.println("QEMU exited with code " + exitCode);
    }
}