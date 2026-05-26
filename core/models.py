# core/models.py
from django.db import models

class SiteSettings(models.Model):
    agency_name = models.CharField(max_length=50, default="M15")
    
    # 🔑 NEW: Logo upload field
    logo = models.ImageField(upload_to="site/logo/", blank=True, null=True, 
                            help_text="Upload your agency logo (PNG/SVG recommended)")
    
    hero_title = models.CharField(max_length=100, default="M15")
    hero_subtitle = models.CharField(max_length=200, default="We Code You Grow")  # ✅ Updated motto
    hero_description = models.TextField(blank=True)
    about_text = models.TextField(blank=True)
    motto = models.CharField(max_length=100, default="We Code You Grow")  # ✅ Global motto
    copyright_text = models.CharField(max_length=150, default="© 2026 M15 Agency. All rights reserved.")
    updated = models.DateTimeField(auto_now=True)

    class Meta:
        verbose_name = "Site Content"
    
    def __str__(self):
        return self.agency_name

class SocialLink(models.Model):
    PLATFORM = [
        ("facebook", "Facebook"), 
        ("instagram", "Instagram"), 
        ("tiktok", "TikTok"),  # ✅ Added TikTok
        ("linkedin", "LinkedIn"), 
        ("github", "GitHub"),
        ("twitter", "Twitter/X")
    ]
    platform = models.CharField(max_length=20, choices=PLATFORM)
    url = models.URLField()
    icon = models.CharField(max_length=30, default="bi-link-45deg", 
                           help_text="Bootstrap Icon class, e.g., 'instagram', 'tiktok'")
    active = models.BooleanField(default=True)
    
    def __str__(self):
        return f"{self.platform} - {self.url}"