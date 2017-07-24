1. Download Docker
2. In command line, run:
  docker run -it -v $(pwd):/data -p 8080:80 klokantech/tileserver-gl south-bend_indiana.mbtiles -c config.json
3. Use jTileDownloader to download the tiles
