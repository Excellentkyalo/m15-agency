from django.contrib import admin

# Register your models here.
from django.contrib import admin
from .models import SiteSettings, SocialLink

@admin.register(SiteSettings)
class SiteSettingsAdmin(admin.ModelAdmin):
    list_display = ('agency_name', 'hero_title', 'updated')

@admin.register(SocialLink)
class SocialLinkAdmin(admin.ModelAdmin):
    list_display = ('platform', 'url', 'active')
    list_filter = ('active',)