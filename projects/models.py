from django.db import models
from django.utils.text import slugify

class ProjectCategory(models.Model):
    name = models.CharField(max_length=50, unique=True)
    slug = models.SlugField(unique=True)
    def __str__(self): return self.name

class Project(models.Model):
    title = models.CharField(max_length=100)
    slug = models.SlugField(unique=True, blank=True)
    description = models.TextField()
    category = models.ForeignKey(ProjectCategory, on_delete=models.SET_NULL, null=True, related_name="projects")
    technologies = models.CharField(max_length=200, blank=True)
    live_url = models.URLField(blank=True)
    github_url = models.URLField(blank=True)
    cover_image = models.ImageField(upload_to="projects/covers/")
    is_featured = models.BooleanField(default=False)
    created = models.DateTimeField(auto_now_add=True)

    def save(self, *args, **kwargs):
        if not self.slug: self.slug = slugify(self.title)
        super().save(*args, **kwargs)

    def __str__(self): return self.title

class ProjectImage(models.Model):
    # related_name="images" prevents reverse query clashes
    project = models.ForeignKey(Project, related_name="images", on_delete=models.CASCADE)
    image = models.ImageField(upload_to="projects/screenshots/")
    alt_text = models.CharField(max_length=100, blank=True)

    def __str__(self): return f"Screenshot for {self.project.title}"