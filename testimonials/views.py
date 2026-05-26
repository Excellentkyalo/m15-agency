# testimonials/views.py
from django.shortcuts import redirect
from django.contrib import messages
from .models import Testimonial

def submit_testimonial(request):
    """Handle testimonial submission form"""
    if request.method == 'POST':
        name = request.POST.get('name')
        role = request.POST.get('role', '')
        message = request.POST.get('message')
        rating = request.POST.get('rating')
        image = request.FILES.get('image')

        # Validate required fields
        if name and message and rating:
            try:
                Testimonial.objects.create(
                    name=name,
                    role=role,
                    message=message,
                    rating=int(rating),
                    image=image,
                    is_approved=False  # Requires admin approval
                )
                messages.success(request, "✅ Thank you! Your review is pending approval.")
            except Exception as e:
                messages.error(request, f"❌ Error: {str(e)}")
        else:
            messages.error(request, "❌ Please fill in name, message, and rating.")
            
    # Redirect back to homepage with anchor
    return redirect(request.META.get('HTTP_REFERER', '/#testimonials'))