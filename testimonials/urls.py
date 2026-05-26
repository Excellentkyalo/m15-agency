# testimonials/urls.py
from django.urls import path
from . import views

urlpatterns = [
    path('submit-review/', views.submit_testimonial, name='submit_testimonial'),
]