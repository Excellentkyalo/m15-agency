from django.db import models

class Testimonial(models.Model):
    name = models.CharField(max_length=100)
    role = models.CharField(max_length=100, help_text="e.g. CEO at TechCorp")
    message = models.TextField()
    rating = models.IntegerField(choices=[(i, str(i)*5) for i in range(1,6)])
    image = models.ImageField(upload_to="testimonials/", blank=True, null=True)
    is_approved = models.BooleanField(default=False)
    created = models.DateTimeField(auto_now_add=True)

    def __str__(self): return f"{self.name} - {'✅' if self.is_approved else '⏳'}"