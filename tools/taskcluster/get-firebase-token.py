# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.

"""
This script talks to the taskcluster secrets service to obtain the
Google Firebase service account token and write it to the .firebase_token
file in the root directory.
"""

import os
import taskcluster
import json

# Get JSON data from taskcluster secrets service
secrets = taskcluster.Secrets({'rootUrl': os.environ['TASKCLUSTER_PROXY_URL']})
data = secrets.get('project/mobile/firefox-tv/tokens')

with open(os.path.join(os.path.dirname(__file__), '../../.firebase_token.json'), 'w') as file:
    json.dump(data['secret']['firebaseToken'], file)

print("Imported google firebase token from secrets service.")
