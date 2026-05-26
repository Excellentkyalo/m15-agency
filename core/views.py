from django.shortcuts import render

# Create your views here.
from django.shortcuts import render
from projects.models import Project, ProjectCategory
from testimonials.models import Testimonial
from contacts.models import ContactMessage
from core.models import SiteSettings, SocialLink
from django.http import HttpResponse
from django.contrib.auth import get_user_model
import os

def home(request):
    settings = SiteSettings.objects.first()
    if not settings: settings = SiteSettings.objects.create()
    
    categories = ProjectCategory.objects.all()
    projects = Project.objects.filter(is_approved=True).order_by("-created")[:12] if hasattr(Project, "is_approved") else Project.objects.order_by("-created")[:12]
    testimonials = Testimonial.objects.filter(is_approved=True)
    socials = SocialLink.objects.filter(active=True)
    
    return render(request, "pages/home.html", {
        "settings": settings,
        "categories": categories,
        "projects": projects,
        "testimonials": testimonials,
        "social_links": socials
    })

def contact_submit(request):
    if request.method == "POST":
        ContactMessage.objects.create(
            name=request.POST.get("name"),
            email=request.POST.get("email"),
            message=request.POST.get("message")
        )
    return home(request)

def create_admin_temp(request):
    """TEMPORARY: Creates superuser. DELETE THIS VIEW AFTER USE."""
    # Simple secret token to prevent public access
    secret = os.environ.get('ADMIN_CREATE_SECRET', 'm15-secure-2026')
    token = request.GET.get('token')
    
    if token != secret:
        return HttpResponse("❌ Access denied", status=403)
    
    User = get_user_model()
    if not User.objects.filter(username='admin').exists():
        User.objects.create_superuser(
            username='admin',
            email='m15digital15@gmail.com',
            password='Munene11989'  # Change this after first login!
        )
        return HttpResponse("✅ Superuser created! NOW DELETE THIS VIEW FROM CODE.")
    return HttpResponse("⚠️ Admin already exists.")