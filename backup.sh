#!/bin/bash
# Run this BEFORE deploying the api-football integration.
# It dumps the full database so predictions and all data are safe.
#
# Usage: ./backup.sh
# Requires: MYSQLHOST, MYSQLPORT, MYSQLDATABASE, MYSQLUSER, MYSQLPASSWORD env vars set

set -e

BACKUP_FILE="backup_$(date +%Y%m%d_%H%M%S).sql"

echo "Creating backup: $BACKUP_FILE"

mysqldump \
  --host="$MYSQLHOST" \
  --port="$MYSQLPORT" \
  --user="$MYSQLUSER" \
  --password="$MYSQLPASSWORD" \
  --single-transaction \
  --routines \
  --triggers \
  "$MYSQLDATABASE" > "$BACKUP_FILE"

echo "Backup complete: $BACKUP_FILE ($(du -h "$BACKUP_FILE" | cut -f1))"
echo ""
echo "To restore if anything goes wrong:"
echo "  mysql --host=\$MYSQLHOST --port=\$MYSQLPORT --user=\$MYSQLUSER --password=\$MYSQLPASSWORD \$MYSQLDATABASE < $BACKUP_FILE"
