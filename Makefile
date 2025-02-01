.PHONY: build test program clean

build:
	mill video.run
	cd quartus; quartus_sh --flow compile tecmo

program:
	cd quartus; quartus_pgm -m jtag -c 1 -o "p;output_files/tecmo.sof@2"

clean:
	rm -rf out rtl/TestPattern.* quartus/db quartus/incremental_db quartus/output_files
