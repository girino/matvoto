#!/bin/bash

realpath() {
    [[ $1 = /* ]] && echo "$1" || echo "$PWD/${1#./}"
}

CURRENT_DIR=$(dirname $0)
CURRENT_DIR=$(realpath $CURRENT_DIR)
BASE_URL=http://agencia.tse.jus.br/estatistica/sead/odsele
if [ x$1 == x ]; then
	ANO=2012
else
	ANO=$1
fi
DETALHE_FILE=detalhe_votacao_munzona_${ANO}.zip
DETALHE_URL=${BASE_URL}/detalhe_votacao_munzona/${DETALHE_FILE}
VOTACAO_FILE=votacao_candidato_munzona_${ANO}.zip
VOTACAO_URL=${BASE_URL}/votacao_candidato_munzona/${VOTACAO_FILE}

TMPDIR="/tmp/$(basename $0).$$.tmp"
TMPFILE="$TMPDIR/$(basename $0).$$.sqlite"
mkdir $TMPDIR
cd $TMPDIR

wget $DETALHE_URL
wget $VOTACAO_URL

unzip -o $DETALHE_FILE
unzip -o $VOTACAO_FILE

cat ./votacao_candidato_munzona_${ANO}_*.txt | iconv -f latin1 -t utf8 | grep PREFEITO > candidatos.txt
cat ./detalhe_votacao_munzona_${ANO}_*.txt | iconv -f latin1 -t utf8 | grep PREFEITO > detalhe_votacao.txt

sqlite3 $TMPFILE < $CURRENT_DIR/create_tables.sql
sqlite3 $TMPFILE < $CURRENT_DIR/create_views.sql
sqlite3 $TMPFILE < $CURRENT_DIR/extract_data.sql
