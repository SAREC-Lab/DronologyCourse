#! /bin/bash

mkdir -p ./ConvertedLogs/

for i in "$@"; do
	export infile="$i"
	
	echo $infile

	export allattr=`echo $infile | sed 's/\./__allattr\./'`

	cat $infile | grep AllAttributes | head -1 /dev/stdin | sed 's/^\(\S*\t\)\(\S*\t\)/\2\1/' | sed 's/\(\(\S\| \)*\t\)\(\(\S\| \)*\t\?\)/\1/g' | sed 's/AllAttributes/time/' > $allattr
	cat $infile | grep AllAttributes | sed 's/^\(\S*\t\)\(\S*\t\)/\2\1/' | sed 's/\(\(\S\| \)*\t\)\(\(\S\| \)*\t\?\)/\3/g' >> $allattr

	export allattrconv=`echo $allattr | sed 's/\./_conv\./'`

	cat $allattr | head -1 /dev/stdin \
		 | sed 's/batterystatus/battery_current\tbattery_voltage\tbattery_level/' \
		 | sed 's/location_global_frame/location_global_y\tlocation_global_x\tlocation_global_z/' \
		 | sed 's/attitude/attitude_y\tattitude_x\tattitude_z/' \
		 | sed 's/home/home_y\thome_x\thome_z/' \
		 | sed 's/gimbalRotation/gimbalRotation_y\tgimbalRotation_x\tgimbalRotation_z/' \
		 | sed 's/\tlocation\t/\tlocation_y\tlocation_x\tlocation_z\t/' \
		 | sed 's/location_local_frame/location_local_y\tlocation_local_x\tlocation_local_z/' \
		 | sed 's/velocity/velocity_y\tvelocity_x\tvelocity_z/' \
		 > $allattrconv
	cat $allattr | sed 's/^time.*//' | awk 'NF' \
		 | sed "s/{'current': \(\S*\), 'voltage': \(\S*\), 'level': \(\S*\)}/\1\t\2\t\3/g" \
		 | sed "s/{'y': //g" | sed "s/, 'x': /\t/g" | sed "s/, 'z': \(\S*\)}/\t\1/g" \
		 | sed 's/^\(....\)_\(..\)_\(..\)__\(..\)-\(..\)-\(..\)_\(...\)/\1-\2-\3 \4:\5:\6.\7/' \
		 >> $allattrconv

	export allattrcsv=`echo $allattr | sed 's/\.\(.*\)/_\1\.csv/'`
	export allattrconvcsv=`echo $allattrconv | sed 's/\.\(.*\)/_\1\.csv/'`

	cat $allattr | sed 's/,/;/g' | sed 's/\t/,/g' > $allattrcsv
	cat $allattrconv | sed 's/,/;/g' | sed 's/\t/,/g' > $allattrconvcsv

	mv $allattr ./ConvertedLogs/
	mv $allattrconv ./ConvertedLogs/
	mv $allattrcsv ./ConvertedLogs/
	mv $allattrconvcsv ./ConvertedLogs/
done
