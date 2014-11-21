#-*-coding:utf8-*-
from django.shortcuts import render
import csv
import os
from datetime import date, timedelta
from django.views.decorators.http import condition
from django.conf import settings
from django.contrib.auth.decorators import login_required
from django.views.decorators.csrf import csrf_exempt, csrf_protect
import logging
import urlparse
from django.http import HttpResponse, HttpResponseRedirect
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.contrib.auth.forms import AuthenticationForm
from django.contrib.sites.models import get_current_site
from django.contrib.auth.views import logout
from django.contrib.auth import REDIRECT_FIELD_NAME, login as auth_login
from django.core.urlresolvers import reverse

import json

from forms import *

# Create your views here.

@csrf_exempt
def login_custom(request):
    code = -1
    redirect_to = request.REQUEST.get(REDIRECT_FIELD_NAME, '')
    if request.method == "POST":
        form = AuthenticationForm(data=request.POST)
        if form.is_valid():
            code = 0
    return HttpResponse(json.dumps(dict(code=code)))


def logout_custom(request):
    return logout(request)


