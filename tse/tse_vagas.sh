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
FILE=consulta_vagas_${ANO}.zip
BASE_FILE=consulta_vagas_${ANO}
URL=${BASE_URL}/consulta_vagas/${FILE}
TABLE=vagas
DB=tse

TMPDIR="/tmp/$(basename $0).${ANO}.tmp"
mkdir $TMPDIR
cd $TMPDIR

wget -c $URL

unzip -o $FILE

echo "deleteing old imports"
mysql --user=root ${DB} <<< "delete from ${TABLE}  where ANO_ELEICAO=${ANO}"
for f in ./${BASE_FILE}_*.txt; do
  echo converting $f
  cat $f | iconv -f latin1 -t utf8 > ${TABLE}.txt
  echo "removing ^M"
  dos2unix ${TABLE}.txt
  mysqlimport --fields-terminated-by=\; --fields-optionally-enclosed-by=\" \
	--local --user=root \
	-C -v --default-character-set=utf8 \
	${DB} ${TABLE}.txt 
  #sleep 2
done
