#!/usr/bin/env python
# 
# 
# 
# Copyright (c) 2008 University of Dundee. 
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
# 
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
# 
# Author: Aleksandra Tarkowska <A(dot)Tarkowska(at)dundee(dot)ac(dot)uk>, 2008.
# 
# Version: 1.0
#

import os.path

from django.conf import settings
from django.conf.urls.defaults import *
from django.views.static import serve
from django.contrib.staticfiles.urls import staticfiles_urlpatterns

# error handler
handler404 = "omeroweb.feedback.views.handler404"
handler500 = "omeroweb.feedback.views.handler500"

# url patterns
urlpatterns = patterns('',
    
    (r'^favicon\.ico$', 'django.views.generic.simple.redirect_to', {'url': '/static/common/image/ome.ico'}),
    
    (r'(?i)^webadmin/', include('omeroweb.webadmin.urls')),
    (r'(?i)^webclient/', include('omeroweb.webclient.urls')),
    (r'(?i)^feedback/', include('omeroweb.feedback.urls')),
    (r'(?i)^webgateway/', include('omeroweb.webgateway.urls')),
    (r'(?i)^webtest/', include('omeroweb.webtest.urls')),    
    (r'(?i)^url/', include('omeroweb.webredirect.urls')),
)

for app in settings.ADDITIONAL_APPS:
    regex = '(?i)^%s/' % app
    urlpatterns += patterns('', (regex, include('omeroweb.%s.urls' % app)),)

urlpatterns += staticfiles_urlpatterns()
