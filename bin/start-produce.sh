WHITE='\033[1;37m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'

echo "${BLUE}[${GREEN}Build${BLUE}] ${WHITE}Start building mock data producer."
cd ../python/
make build
cd ../python-dist/
python dist/mock/producer.py