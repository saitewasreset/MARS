package io.github.dpetersanderson.mars.mips.instructions;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.mips.instructions.syscalls.Syscall;
import io.github.dpetersanderson.mars.mips.instructions.syscalls.SyscallNumberOverride;
import io.github.dpetersanderson.mars.mips.instructions.syscalls.SyscallRegistry;
import java.util.ArrayList;
import java.util.List;

/*
Copyright (c) 2003-2006,  Pete Sanderson and Kenneth Vollmar

Developed by Pete Sanderson (psanderson@otterbein.edu)
and Kenneth Vollmar (kenvollmar@missouristate.edu)

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject
to the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

(MIT license, http://www.opensource.org/licenses/mit-license.html)
 */

/****************************************************************************/
/* This class provides functionality to bring external Syscall definitions
 * into MARS.  This permits anyone with knowledge of the Mars public interfaces,
 * in particular of the Memory and Register classes, to write custom MIPS syscall
 * functions. This is adapted from the ToolLoader class, which is in turn adapted
 * from Bret Barker's GameServer class from the book "Developing Games In Java".
 */

class SyscallLoader {
    private final List<Syscall> syscallList = SyscallRegistry.getSyscalls();

    // Will get any syscall number override specifications from MARS config file and
    // process them.  This will alter syscallList entry for affected names.
    private ArrayList<Syscall> processSyscallNumberOverrides(ArrayList<Syscall> syscallList) {
        ArrayList<SyscallNumberOverride> overrides = new Globals().getSyscallOverrides();

        for (SyscallNumberOverride override : overrides) {
            boolean match = false;

            for (Syscall syscall : syscallList) {
                if (override.getName().equals(syscall.getName())) {
                    // we have a match to service name, assign new number
                    syscall.setNumber(override.getNumber());
                    match = true;
                }
            }
            if (!match) {
                throw new RuntimeException("Error: syscall name '" + override.getName()
                        + "' in config file does not match any name in syscall list");
            }
        }

        // Wait until end to check for duplicate numbers.  To do so earlier
        // would disallow for instance the exchange of numbers between two
        // services.  This is N-squared operation but N is small.
        // This will also detect duplicates that accidently occur from addition
        // of a new Syscall subclass to the collection, even if the config file
        // does not contain any overrides.
        boolean duplicates = false;

        for (int i = 0; i < syscallList.size(); i++) {
            Syscall syscallA = syscallList.get(i);
            for (int j = i + 1; j < syscallList.size(); j++) {
                Syscall syscallB = syscallList.get(j);
                if (syscallA.getNumber() == syscallB.getNumber()) {
                    System.out.println("Error: syscalls " + syscallA.getName() + " and " + syscallB.getName()
                            + " are both assigned same number " + syscallA.getNumber());
                    duplicates = true;
                }
            }
        }

        if (duplicates) {
            throw new RuntimeException("Error: detected duplicate syscalls");
        }
        return syscallList;
    }

    /*
     * Method to find Syscall object associated with given service number.
     * Returns null if no associated object found.
     */
    Syscall findSyscall(int number) {
        // linear search is OK since number of syscalls is small.
        Syscall service, match = null;

        for (Syscall syscall : syscallList) {
            service = syscall;
            if (service.getNumber() == number) {
                match = service;
            }
        }
        return match;
    }
}
