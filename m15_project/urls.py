"""
URL configuration for m15_project.
"""
from django.contrib import admin
from django.urls import path, include
from django.conf import settings
from django.conf.urls.static import static
from core.views import home, contact_submit
from core import views  # or: from core.views import home, contact_submit, create_admin_temp
urlpatterns = [
    # 🔐 Admin
    path('admin/', admin.site.urls),
    
    # 🌐 Frontend
    path('', home, name='home'),
    path('send-contact/', contact_submit, name='contact_submit'),
    
    # 📊 Dashboard
    path('dashboard/', include('dashboard.urls')),
    path('dashboard/login/', admin.site.login, name='login'),
    
    # 💬 Testimonials (Root-level include, matches your template tag exactly)
    path('', include('testimonials.urls')),

    # TEMPORARY: Delete this line after creating admin
path('create-admin-temp/', views.create_admin_temp),
]

# Serve media files in development
if settings.DEBUG:
    urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)