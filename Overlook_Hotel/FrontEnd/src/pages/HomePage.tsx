import React from "react";
import "./HomePage.css";

const HomePage: React.FC = () => {
  return (
    <div className="homepage">
      {/* Navbar */}
      <header className="navbar">
        <div className="logo">Hôtel Paradis</div>
        <nav>
          <a href="#">Accueil</a>
          <a href="#">Chambres</a>
          <a href="#">Services</a>
          <a href="#">Contact</a>
        </nav>
      </header>

      {/* Hero Section */}
      <section className="hero">
        <div className="overlay"></div>
        <div className="hero-content">
          <h1>Vivez l’expérience du luxe</h1>
          <p>Un séjour inoubliable dans un cadre unique au cœur de la ville.</p>
          <button>Réserver maintenant</button>
        </div>
      </section>

      {/* Chambres */}
      <section className="rooms">
        <h2>Nos Chambres</h2>
        <div className="room-list">
          <div className="room-card">
            <img src="https://images.unsplash.com/photo-1600585154206-1b4e18d5f7c3?auto=format&fit=crop&w=800&q=80" alt="Chambre Standard" />
            <h3>Chambre Standard</h3>
            <p>Confort moderne à prix abordable.</p>
          </div>
          <div className="room-card">
            <img src="https://images.unsplash.com/photo-1600585154313-1c1efb0b9d3c?auto=format&fit=crop&w=800&q=80" alt="Suite Luxe" />
            <h3>Suite Luxe</h3>
            <p>Élégance et raffinement avec vue panoramique.</p>
          </div>
          <div className="room-card">
            <img src="https://images.unsplash.com/photo-1600585153951-3d23dfe6c4d2?auto=format&fit=crop&w=800&q=80" alt="Villa Privée" />
            <h3>Villa Privée</h3>
            <p>Un espace exclusif pour un séjour d’exception.</p>
          </div>
        </div>
      </section>

      {/* Services */}
      <section className="services">
        <h2>Nos Services</h2>
        <div className="service-list">
          <div className="service-card">
            <h3>Spa & Bien-être</h3>
            <p>Offrez-vous un moment de détente unique.</p>
          </div>
          <div className="service-card">
            <h3>Restaurant Gastronomique</h3>
            <p>Découvrez une cuisine raffinée aux saveurs locales.</p>
          </div>
          <div className="service-card">
            <h3>Piscine & Fitness</h3>
            <p>Détendez-vous ou gardez la forme durant votre séjour.</p>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="cta">
        <h2>Réservez dès maintenant votre séjour de rêve</h2>
        <button>Réserver</button>
      </section>

      {/* Footer */}
      <footer className="footer">
        <p>© 2025 Hôtel Paradis - Tous droits réservés</p>
        <p>📍 Paris, France | 📞 +33 1 23 45 67 89</p>
      </footer>
    </div>
  );
};

export default HomePage;
