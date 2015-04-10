#!/bin/bash

realpath() {
    [[ $1 = /* ]] && echo "$1" || echo "$PWD/${1#./}"
}

CURRENT_DIR=$(dirname $0)
CURRENT_DIR=$(realpath $CURRENT_DIR)
BASE_URL=http://agencia.tse.jus.br/estatistica/sead/odsele
if [ x$1 == x ]; then
	ANO=2014
else
	ANO=$1
fi
DETALHE_FILE=detalhe_votacao_munzona_${ANO}.zip
DETALHE_URL=${BASE_URL}/detalhe_votacao_munzona/${DETALHE_FILE}
VOTACAO_FILE=votacao_candidato_munzona_${ANO}.zip
VOTACAO_URL=${BASE_URL}/votacao_candidato_munzona/${VOTACAO_FILE}

TMPDIR="/tmp/$(basename $0).${ANO}.tmp"
mkdir $TMPDIR
cd $TMPDIR

wget -c $DETALHE_URL
wget -c $VOTACAO_URL

unzip -o $DETALHE_FILE
#sleep 2
unzip -o $VOTACAO_FILE
#sleep 2

echo "deleteing old imports"
mysql --user=root tse <<< "delete from candidato where ANO_ELEICAO=${ANO}"
for f in ./votacao_candidato_munzona_${ANO}_*.txt; do
  echo converting $f
  cat $f | iconv -f latin1 -t utf8 > candidato.txt
  echo "removing ^M"
  dos2unix candidato.txt
  mysqlimport --fields-terminated-by=\; --fields-optionally-enclosed-by=\" \
	--local --user=root \
	-C -v --default-character-set=utf8 \
	tse candidato.txt 
  #sleep 2
done


#rm -f detalhe_votacao.txt
#touch detalhe_votacao.txt
#for f in ./detalhe_votacao_munzona_${ANO}_*.txt; do
#  cat $f | iconv -f latin1 -t utf8 >> detalhe_votacao.txt
#  sleep 2
#done

#sqlite3 $TMPFILE < $CURRENT_DIR/create_tables.sql
#sqlite3 $TMPFILE < $CURRENT_DIR/create_views.sql
#sqlite3 $TMPFILE < $CURRENT_DIR/extract_data.sql
