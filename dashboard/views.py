from django.shortcuts import render
from django.contrib.auth.decorators import login_required
from projects.models import Project
from testimonials.models import Testimonial
from contacts.models import ContactMessage

@login_required(login_url='/admin/login/')  # Redirects to Django login if not authenticated
def dashboard(request):
    return render(request, 'dashboard/index.html', {
        'projects_count': Project.objects.count(),
        'testimonials_count': Testimonial.objects.filter(is_approved=True).count(),
        'pending_testimonials': Testimonial.objects.filter(is_approved=False).count(),
        'messages_count': ContactMessage.objects.filter(is_read=False).count(),
    })