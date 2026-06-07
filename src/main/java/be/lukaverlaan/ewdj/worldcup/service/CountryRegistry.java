package be.lukaverlaan.ewdj.worldcup.service;

import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component("countryRegistry")
public class CountryRegistry {

    private static final String B = "/images/badges/";

    private static final Map<String, CountryInfo> DATA = Map.ofEntries(
        // UEFA (16)
        Map.entry("France",                 new CountryInfo(2,  B+"france.png",        "Frankrijk",         "France")),
        Map.entry("Spain",                  new CountryInfo(3,  B+"spain.png",          "Spanje",            "Espagne")),
        Map.entry("England",                new CountryInfo(5,  B+"england.png",        "Engeland",          "Angleterre")),
        Map.entry("Portugal",               new CountryInfo(6,  B+"portugal.png",       "Portugal",          "Portugal")),
        Map.entry("Netherlands",            new CountryInfo(8,  B+"netherlands.png",    "Nederland",         "Pays-Bas")),
        Map.entry("Belgium",                new CountryInfo(10, B+"belgium.png",        "België",            "Belgique")),
        Map.entry("Germany",                new CountryInfo(12, B+"germany.png",        "Duitsland",         "Allemagne")),
        Map.entry("Croatia",                new CountryInfo(13, B+"croatia.png",        "Kroatië",           "Croatie")),
        Map.entry("Switzerland",            new CountryInfo(19, B+"switzerland.png",    "Zwitserland",       "Suisse")),
        Map.entry("Austria",                new CountryInfo(26, B+"austria.png",        "Oostenrijk",        "Autriche")),
        Map.entry("Norway",                 new CountryInfo(32, B+"norway.png",         "Noorwegen",         "Norvège")),
        Map.entry("Scotland",               new CountryInfo(39, B+"scotland.png",       "Schotland",         "Écosse")),
        Map.entry("Czech Republic",         new CountryInfo(40, B+"czech-republic.png", "Tsjechië",          "République tchèque")),
        Map.entry("Bosnia and Herzegovina", new CountryInfo(51, B+"bosnia.png",         "Bosnië-Herzegovina","Bosnie-Herzégovine")),
        Map.entry("Turkey",                 new CountryInfo(24, B+"turkey.png",         "Turkije",           "Turquie")),
        Map.entry("Sweden",                 new CountryInfo(35, B+"sweden.png",         "Zweden",            "Suède")),
        Map.entry("Poland",                 new CountryInfo(21, B+"poland.png",         "Polen",             "Pologne")),
        // CONMEBOL (6)
        Map.entry("Argentina",              new CountryInfo(1,  B+"argentina.png",      "Argentinië",        "Argentine")),
        Map.entry("Brazil",                 new CountryInfo(4,  B+"brazil.png",         "Brazilië",          "Brésil")),
        Map.entry("Colombia",               new CountryInfo(14, B+"colombia.png",       "Colombia",          "Colombie")),
        Map.entry("Uruguay",                new CountryInfo(17, B+"uruguay.png",        "Uruguay",           "Uruguay")),
        Map.entry("Ecuador",                new CountryInfo(23, B+"ecuador.png",        "Ecuador",           "Équateur")),
        Map.entry("Paraguay",               new CountryInfo(38, B+"paraguay.png",       "Paraguay",          "Paraguay")),
        // CONCACAF (6)
        Map.entry("United States",          new CountryInfo(16, B+"united-states.png",  "Verenigde Staten",  "États-Unis")),
        Map.entry("USA",                    new CountryInfo(16, B+"united-states.png",  "Verenigde Staten",  "États-Unis")),
        Map.entry("Mexico",                 new CountryInfo(15, B+"mexico.png",         "Mexico",            "Mexique")),
        Map.entry("Canada",                 new CountryInfo(30, B+"canada.png",         "Canada",            "Canada")),
        Map.entry("Panama",                 new CountryInfo(33, B+"panama.png",         "Panama",            "Panama")),
        Map.entry("Curaçao",                new CountryInfo(81, B+"curacao.png",        "Curaçao",           "Curaçao")),
        Map.entry("Haiti",                  new CountryInfo(83, B+"haiti.png",          "Haïti",             "Haïti")),
        // CAF (10)
        Map.entry("Morocco",                new CountryInfo(9,  B+"morocco.png",        "Marokko",           "Maroc")),
        Map.entry("Senegal",                new CountryInfo(11, B+"senegal.png",        "Senegal",           "Sénégal")),
        Map.entry("Egypt",                  new CountryInfo(29, B+"egypt.png",          "Egypte",            "Égypte")),
        Map.entry("Ivory Coast",            new CountryInfo(27, B+"ivory-coast.png",    "Ivoorkust",         "Côte d'Ivoire")),
        Map.entry("South Africa",           new CountryInfo(66, B+"south-africa.png",   "Zuid-Afrika",       "Afrique du Sud")),
        Map.entry("Ghana",                  new CountryInfo(57, B+"ghana.png",          "Ghana",             "Ghana")),
        Map.entry("Tunisia",                new CountryInfo(44, B+"tunisia.png",        "Tunesië",           "Tunisie")),
        Map.entry("DR Congo",               new CountryInfo(46, B+"dr-congo.png",       "DR Congo",          "RD Congo")),
        Map.entry("Algeria",                new CountryInfo(36, B+"algeria.png",        "Algerije",          "Algérie")),
        Map.entry("Cape Verde",             new CountryInfo(79, B+"cape-verde.png",     "Kaapverdië",        "Cap-Vert")),
        // AFC (9)
        Map.entry("Japan",                  new CountryInfo(18, B+"japan.png",          "Japan",             "Japon")),
        Map.entry("South Korea",            new CountryInfo(22, B+"south-korea.png",    "Zuid-Korea",        "Corée du Sud")),
        Map.entry("Iran",                   new CountryInfo(20, B+"iran.png",           "Iran",              "Iran")),
        Map.entry("Australia",              new CountryInfo(25, B+"australia.png",      "Australië",         "Australie")),
        Map.entry("Saudi Arabia",           new CountryInfo(56, B+"saudi-arabia.png",   "Saoedi-Arabië",     "Arabie Saoudite")),
        Map.entry("Uzbekistan",             new CountryInfo(50, B+"uzbekistan.png",     "Oezbekistan",       "Ouzbékistan")),
        Map.entry("Iraq",                   new CountryInfo(62, B+"iraq.png",           "Irak",              "Irak")),
        Map.entry("Jordan",                 new CountryInfo(87, B+"jordan.png",         "Jordanië",          "Jordanie")),
        Map.entry("Qatar",                  new CountryInfo(72, B+"qatar.png",          "Qatar",             "Qatar")),
        // OFC (1)
        Map.entry("New Zealand",            new CountryInfo(100, B+"new-zealand.png",   "Nieuw-Zeeland",     "Nouvelle-Zélande"))
    );

    public CountryInfo get(String country) {
        return DATA.get(country);
    }

    public String getDisplayName(String country, Locale locale) {
        if (country == null) return "";
        CountryInfo info = DATA.get(country);
        if (info == null) return country;
        String lang = locale.getLanguage();
        if ("nl".equals(lang) && info.nameNl() != null) return info.nameNl();
        if ("fr".equals(lang) && info.nameFr() != null) return info.nameFr();
        return country;
    }

    public List<CountryEntry> getAllSorted() {
        return DATA.entrySet().stream()
            .sorted(Comparator.comparingInt(e -> e.getValue().rank()))
            .map(e -> new CountryEntry(
                e.getKey(),
                e.getValue().rank(),
                e.getValue().logoUrl(),
                e.getValue().nameNl(),
                e.getValue().nameFr()))
            .collect(Collectors.toList());
    }
}
