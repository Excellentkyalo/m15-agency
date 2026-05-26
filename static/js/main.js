document.addEventListener('DOMContentLoaded', () => {
  
  // ==================== CUSTOM CURSOR ====================
  const cursorDot = document.getElementById('cursorDot');
  const cursorOutline = document.getElementById('cursorOutline');
  
  let mouseX = 0, mouseY = 0;
  let outlineX = 0, outlineY = 0;
  
  // Track mouse position
  document.addEventListener('mousemove', (e) => {
    mouseX = e.clientX;
    mouseY = e.clientY;
    
    // Dot follows instantly
    cursorDot.style.left = mouseX + 'px';
    cursorDot.style.top = mouseY + 'px';
  });
  
  // Smooth outline follow with easing
  function animateCursor() {
    outlineX += (mouseX - outlineX) * 0.15;
    outlineY += (mouseY - outlineY) * 0.15;
    
    cursorOutline.style.left = outlineX + 'px';
    cursorOutline.style.top = outlineY + 'px';
    
    requestAnimationFrame(animateCursor);
  }
  animateCursor();
  
  // Hover effect on interactive elements
  const interactiveElements = document.querySelectorAll(
    'a, button, .btn-glow, .btn-glass, .service-card, .bento-item, ' +
    '.social-icon, .nav-link-custom, .mob-link, .filter-btn, input, textarea, select'
  );
  
  interactiveElements.forEach(el => {
    el.addEventListener('mouseenter', () => cursorOutline.classList.add('hover'));
    el.addEventListener('mouseleave', () => cursorOutline.classList.remove('hover'));
  });
  
  // ==================== THEME TOGGLE ====================
  const themeToggle = document.getElementById('themeToggle');
  const mobileThemeToggle = document.getElementById('mobileThemeToggle');
  const html = document.documentElement;
  
  // Load saved theme
  const savedTheme = localStorage.getItem('m15-theme') || 'dark';
  html.setAttribute('data-theme', savedTheme);
  updateThemeIcon(savedTheme);
  
  function updateThemeIcon(theme) {
    const icon = theme === 'dark' ? 'bi bi-sun' : 'bi bi-moon';
    if (themeToggle) themeToggle.querySelector('i').className = icon;
    if (mobileThemeToggle) mobileThemeToggle.querySelector('i').className = icon;
  }
  
  function toggleTheme() {
    const current = html.getAttribute('data-theme');
    const next = current === 'dark' ? 'light' : 'dark';
    html.setAttribute('data-theme', next);
    localStorage.setItem('m15-theme', next);
    updateThemeIcon(next);
  }
  
  if (themeToggle) themeToggle.addEventListener('click', toggleTheme);
  if (mobileThemeToggle) mobileThemeToggle.addEventListener('click', toggleTheme);
  
  // ==================== NAVBAR SCROLL EFFECT ====================
  const navbar = document.getElementById('navbar');
  window.addEventListener('scroll', () => {
    if (window.scrollY > 50) {
      navbar.classList.add('scrolled');
    } else {
      navbar.classList.remove('scrolled');
    }
  });
  
  // ==================== MOBILE MENU TOGGLE ====================
  const navToggle = document.getElementById('navToggle');
  const mobileMenu = document.getElementById('mobileMenu');
  
  if (navToggle && mobileMenu) {
    navToggle.addEventListener('click', () => {
      mobileMenu.classList.toggle('open');
      const icon = navToggle.querySelector('i');
      icon.className = mobileMenu.classList.contains('open') ? 'bi bi-x-lg' : 'bi bi-list';
    });
    
    // Close menu when clicking a link
    document.querySelectorAll('.mob-link').forEach(link => {
      link.addEventListener('click', () => {
        mobileMenu.classList.remove('open');
        navToggle.querySelector('i').className = 'bi bi-list';
      });
    });
  }
  
  // ==================== SCROLL REVEAL ANIMATIONS ====================
  const revealElements = document.querySelectorAll('.reveal');
  
  const revealObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('active');
        revealObserver.unobserve(entry.target); // Only animate once
      }
    });
  }, { threshold: 0.15, rootMargin: '0px 0px -50px 0px' });
  
  revealElements.forEach(el => revealObserver.observe(el));
  
  // ==================== SMOOTH SCROLL FOR ANCHOR LINKS ====================
  document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
      e.preventDefault();
      const target = document.querySelector(this.getAttribute('href'));
      if (target) {
        const offsetTop = target.offsetTop - 80; // Account for navbar
        window.scrollTo({ top: offsetTop, behavior: 'smooth' });
      }
    });
  });
  
  // ==================== FILTER BUTTONS (Portfolio) ====================
  const filterBtns = document.querySelectorAll('.filter-btn');
  const bentoItems = document.querySelectorAll('.bento-item');
  
  filterBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      // Update active state
      filterBtns.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      
      const filter = btn.getAttribute('data-filter');
      
      bentoItems.forEach((item, index) => {
        const categories = item.getAttribute('data-category') || '';
        const shouldShow = filter === 'all' || categories.includes(filter);
        
        // Animate hide/show
        if (shouldShow) {
          item.style.display = 'block';
          setTimeout(() => {
            item.style.opacity = '1';
            item.style.transform = 'scale(1)';
          }, index * 50);
        } else {
          item.style.opacity = '0';
          item.style.transform = 'scale(0.95)';
          setTimeout(() => {
            item.style.display = 'none';
          }, 300);
        }
      });
    });
  });
  
  // ==================== CONTACT FORM SUBMIT ====================
  const contactForm = document.getElementById('contactForm');
  if (contactForm) {
    contactForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const submitBtn = contactForm.querySelector('button[type="submit"]');
      const originalText = submitBtn.innerHTML;
      
      submitBtn.innerHTML = '<i class="bi bi-hourglass-split me-2"></i>Sending...';
      submitBtn.disabled = true;
      
      try {
        const formData = new FormData(contactForm);
        const response = await fetch(contactForm.action, {
          method: 'POST',
          body: formData,
          headers: { 'X-Requested-With': 'XMLHttpRequest' }
        });
        
        if (response.ok) {
          submitBtn.innerHTML = '<i class="bi bi-check-circle me-2"></i>Sent!';
          submitBtn.classList.add('btn-success');
          contactForm.reset();
          setTimeout(() => {
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
            submitBtn.classList.remove('btn-success');
          }, 3000);
        }
      } catch (error) {
        console.error('Form error:', error);
        submitBtn.innerHTML = '<i class="bi bi-exclamation-circle me-2"></i>Try Again';
        setTimeout(() => {
          submitBtn.innerHTML = originalText;
          submitBtn.disabled = false;
        }, 2000);
      }
    });
  }
  
});
  // ==================== STAR RATING (GUARANTEED CLICKABLE) ====================
const starContainer = document.getElementById('starRating');
const ratingInput = document.getElementById('ratingInput');
const ratingError = document.getElementById('ratingError');

if (starContainer && ratingInput) {
  const stars = starContainer.querySelectorAll('span');
  
  stars.forEach(star => {
    star.addEventListener('click', (e) => {
      e.preventDefault(); // Prevent form submission interference
      const val = parseInt(star.getAttribute('data-val'));
      ratingInput.value = val;
      ratingError.style.display = 'none';
      
      stars.forEach((s, i) => {
        s.classList.toggle('active', i < val);
      });
    });

    star.addEventListener('mouseenter', () => {
      const val = parseInt(star.getAttribute('data-val'));
      stars.forEach((s, i) => s.classList.toggle('active', i < val));
    });
  });

  // Reset hover state when mouse leaves
  starContainer.addEventListener('mouseleave', () => {
    const current = parseInt(ratingInput.value);
    stars.forEach((s, i) => s.classList.toggle('active', i < current));
  });
}

// Validate rating before form submits
const reviewForm = document.getElementById('reviewForm');
if (reviewForm) {
  reviewForm.addEventListener('submit', (e) => {
    if (!ratingInput.value || ratingInput.value === '0') {
      e.preventDefault();
      ratingError.style.display = 'block';
      starContainer.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
  });
}