#!/bin/bash

cd quartus
quartus_sh --flow compile tecmo
quartus_pgm -m jtag -c 1 -o "p;output_files/tecmo.sof@2"
