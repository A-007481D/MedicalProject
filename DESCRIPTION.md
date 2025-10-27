# :herb: Attari — AI Traditional Remedy Analyzer

> **“Know your remedy — safe, local, and evidence-backed.”**
> Bridging Moroccan ancestral medicine with modern AI insight.

---

## :stethoscope: Overview

**Genba** is an AI-powered platform that analyzes Moroccan **traditional remedies** (herbal mixtures, oils, home recipes) to ensure **safety, scientific validation, and awareness**.

Users can type or photograph a remedy, and Genba identifies ingredients, checks for **toxicity or drug interactions**, and explains the **scientific background** of each component — all in **Darija, Arabic, or French**.

This project aims to bridge **ancestral Moroccan healing wisdom** with **modern artificial intelligence**, empowering people to make safe, informed health decisions.

---

## :warning: Problem Statement

Traditional medicine is deeply embedded in Moroccan culture. However:

* Many people rely on **oral traditions** or **social media recipes** without medical validation.
* Herbal mixtures can be **unsafe**, cause **toxic reactions**, or **interact dangerously** with pharmaceuticals.
* There is **no unified digital resource** that bridges **traditional Moroccan remedies** with **modern medical science**.

### :flag_ma: Local Context

* Over 60% of Moroccans use traditional remedies regularly.
* Reports of poisoning and allergic reactions have been increasing.
* Health literacy and access to professional advice are limited in many rural areas.

---

## :bulb: Solution

**Genba** is a multilingual **AI remedy analyzer** that helps users:

1. **Identify** herbal ingredients (from text or images).
2. **Analyze** safety and potential drug interactions.
3. **Explain** the scientific properties and cultural uses in accessible language.
4. **Educate** users through visual feedback, risk indicators, and verified sources.
5. **Encourage** evidence-based self-care without rejecting traditional culture.

> :white_check_mark: All software-based — no special hardware or IoT devices required.
> Fully functional as a **web or mobile Progressive Web App (PWA)**.

---

## :sparkles: Core Features (MVP)

| #  | Feature                     | Description                                                                                                                 |
| :- | :-------------------------- | :-------------------------------------------------------------------------------------------------------------------------- |
| 1  | **Text Remedy Parsing**     | User enters a recipe (e.g., “chiba + lemon for fever”). NLP extracts ingredients in Darija/French/Arabic.                   |
| 2  | **Image Herb Recognition**  | Upload a photo of a plant or mixture — AI suggests top herb matches.                                                        |
| 3  | **Safety Checker**          | Checks for known toxicity or risky combinations from a curated herb database.                                               |
| 4  | **Drug Interaction Alerts** | Warns users if the remedy conflicts with common medications (e.g., insulin, anticoagulants).                                |
| 5  | **Scientific Explanation**  | Explains what each herb does, when it helps, and when it’s dangerous — in user-friendly language.                           |
| 6  | **Trust & Risk Score**      | Color-coded safety meter (:green_circle: Safe, :orange_circle: Caution, :red_circle: Dangerous) with reasoning and sources. |
| 7  | **PDF / Shareable Report**  | Generates a summary report for users or clinics.                                                                            |

---

## :seedling: Bonus Features (Stretch Goals)

| #  | Bonus Feature                                 | Description                                                            |
| :- | :-------------------------------------------- | :--------------------------------------------------------------------- |
| 1  | **Community Remedy Feed**                     | Users share remedies; Genba auto-analyzes and rates them for safety.   |
| 2  | **Herb Glossary (Darija / Amazigh / French)** | Cross-reference tool for local and scientific herb names.              |
| 3  | **Offline Mode**                              | Works with a small local herb DB for rural or low-connectivity users.  |
| 4  | **WhatsApp / Voice Assistant**                | Allows users to talk to Genba in Darija (using Whisper + LLM).         |
| 5  | **Clinic API**                                | For NGOs or local clinics to use the analyzer in patient intake forms. |

---

## :brain: Architecture

User → Frontend (React/Tailwind)
↓
Backend API (FastAPI/Flask)
↓

Herb & Drug DB (PostgreSQL)

NLP Parsing (GPT / LLaMA / Rule Engine)

Vision Model (Hugging Face)
↓
Result Formatter → Report Generator (PDF/JSON)

---

## :toolbox: Tech Stack

| Layer        | Technology                                                        |
| :----------- | :---------------------------------------------------------------- |
| **Frontend** | React + Tailwind CSS (responsive, mobile-first PWA)               |
| **Backend**  | FastAPI (Python) / Flask                                          |
| **AI/NLP**   | OpenAI GPT / Llama 3 / Mistral (text understanding & explanation) |
| **Vision**   | Hugging Face Image Classifier (EfficientNet/MobileNet)            |
| **Database** | PostgreSQL / SQLite (for herbs, drugs, and interactions)          |
| **Storage**  | S3 / Cloudinary for images                                        |
| **Auth**     | Firebase Auth or JWT                                              |
| **Hosting**  | Vercel (frontend), Render / Fly.io (backend)                      |
| **Docs**     | Swagger UI + Markdown documentation                               |

---

## :herb: Dataset Plan

**Herb Knowledge Base (JSON structure):**

```json
{
  "common_name": "Chiba",
  "scientific_name": "Artemisia absinthium",
  "local_names": ["Chiba", "شيبة", "Shiba"],
  "uses": ["Fever", "Digestive aid"],
  "side_effects": ["Can cause nausea, allergic reactions"],
  "contraindications": ["Pregnancy", "Liver issues"],
  "drug_interactions": ["Anticoagulants", "Diabetes medication"],
  "evidence_sources": ["WHO Monographs", "PubMed Studies"]
}
```

---

## 🔐 Ethics & Privacy

❌ No diagnoses — educational tool only.

🔒 Minimal data collection; user anonymity respected.

🧠 Clear disclaimer: “This tool provides informational analysis only. Always consult a healthcare professional.”

🚨 “Red Flag” feature: recommends contacting clinics for severe cases

---

## 🧩 User Flow

1. **Enter or photograph a remedy**
   Example: “I mixed chiba and lemon for fever.”

2. **Upload optional image.**
   AI identifies and analyzes ingredients.

3. **NLP extracts herbs and quantities.**
   Vision identifies herb type if image uploaded.

4. **Get your safety analysis.**
   Risk meter (Safe / Caution / Dangerous)
   Simple explanations and references.

5. **Optional: add medications.**
   Checks interactions with common drugs.

6. **Download / Share report.**
   Generates PDF or summary card.
