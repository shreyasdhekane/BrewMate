const BASES = [
  { id: "espresso", name: "Espresso", price: 2.5 },
  { id: "hot_chocolate", name: "Hot Chocolate", price: 3.0 },
  { id: "iced_coffee", name: "Iced Coffee", price: 3.25 },
  { id: "caramel_frap", name: "Caramel Frappuccino", price: 4.5 },
  { id: "mixed_black", name: "Mixed Black Coffee", price: 2.75 },
];

const MILKS = [
  { id: "whole", name: "Whole Milk", price: 0 },
  { id: "two_percent", name: "2% Milk", price: 0 },
  { id: "one_percent", name: "1% Milk", price: 0 },
  { id: "skim", name: "Skim Milk", price: 0 },
  { id: "oat", name: "Oat Milk", price: 0.65 },
  { id: "almond", name: "Almond Milk", price: 0.65 },
  { id: "soy", name: "Soy Milk", price: 0.65 },
];

const ADDONS = [
  { id: "vanilla", name: "Vanilla Syrup", price: 0.6 },
  { id: "extra_shot", name: "Extra Shot", price: 1.0 },
  { id: "whip", name: "Whip Cream", price: 0.75 },
  { id: "caramel_drizzle", name: "Caramel Drizzle", price: 0.7 },
];

const state = {
  base: BASES[0].id,
  milk: MILKS[0].id,
  addons: new Set(),
};

function formatPrice(value) {
  return "$" + value.toFixed(2);
}

function renderGroup(containerId, items, selected, onSelect, multi) {
  const container = document.getElementById(containerId);
  container.innerHTML = "";

  items.forEach((item) => {
    const isSelected = multi ? selected.has(item.id) : selected === item.id;

    const btn = document.createElement("button");
    btn.type = "button";
    btn.className = "pill";
    btn.setAttribute("aria-pressed", String(isSelected));

    const label = document.createElement("span");
    label.textContent = item.name;
    btn.appendChild(label);

    if (item.price > 0) {
      const tag = document.createElement("span");
      tag.className = "price-tag";
      tag.textContent = "+" + item.price.toFixed(2);
      btn.appendChild(tag);
    }

    btn.addEventListener("click", () => onSelect(item));
    container.appendChild(btn);
  });
}

function renderPills() {
  renderGroup(
    "base-options",
    BASES,
    state.base,
    (item) => {
      state.base = item.id;
      renderPills();
      renderTicket();
    },
    false,
  );

  renderGroup(
    "milk-options",
    MILKS,
    state.milk,
    (item) => {
      state.milk = item.id;
      renderPills();
      renderTicket();
    },
    false,
  );

  renderGroup(
    "addon-options",
    ADDONS,
    state.addons,
    (item) => {
      if (state.addons.has(item.id)) {
        state.addons.delete(item.id);
      } else {
        state.addons.add(item.id);
      }
      renderPills();
      renderTicket();
    },
    true,
  );
}

function buildLine(name, price, isAddon) {
  const row = document.createElement("div");
  row.className = "ticket-line" + (isAddon ? " addon" : "");

  const nameEl = document.createElement("span");
  nameEl.className = "name";
  nameEl.textContent = name;

  const priceEl = document.createElement("span");
  priceEl.className = "price";
  priceEl.textContent = price;

  row.appendChild(nameEl);
  row.appendChild(priceEl);
  return row;
}

function renderTicket() {
  const base = BASES.find((b) => b.id === state.base);
  const milk = MILKS.find((m) => m.id === state.milk);
  const addons = ADDONS.filter((a) => state.addons.has(a.id));

  const linesContainer = document.getElementById("ticket-lines");
  linesContainer.innerHTML = "";

  linesContainer.appendChild(
    buildLine(`1\u00d7 ${base.name}`, formatPrice(base.price), false),
  );
  linesContainer.appendChild(
    buildLine(
      `+ ${milk.name}`,
      milk.price > 0 ? formatPrice(milk.price) : "\u2014",
      true,
    ),
  );

  addons.forEach((addon) => {
    linesContainer.appendChild(
      buildLine(`+ ${addon.name}`, formatPrice(addon.price), true),
    );
  });

  const total =
    base.price + milk.price + addons.reduce((sum, a) => sum + a.price, 0);
  document.getElementById("ticket-total").textContent = formatPrice(total);
}

function setTicketNumber() {
  const num = Math.floor(Math.random() * 899) + 100;
  document.getElementById("ticket-number").textContent = String(num);
}

// Scroll reveal

function initRevealObserver() {
  const reduceMotion = window.matchMedia(
    "(prefers-reduced-motion: reduce)",
  ).matches;
  const items = document.querySelectorAll(".reveal");

  if (reduceMotion) {
    items.forEach((el) => el.classList.add("in-view"));
    return;
  }

  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add("in-view");
          observer.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.15 },
  );

  items.forEach((el) => observer.observe(el));
}

// Init

document.addEventListener("DOMContentLoaded", () => {
  renderPills();
  renderTicket();
  setTicketNumber();
  initRevealObserver();
});
