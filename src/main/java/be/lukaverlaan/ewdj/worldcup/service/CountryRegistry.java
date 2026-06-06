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

    // 48 FIFA WK 2026 Deelnemers - (rank, logoUrl, naam in het Nederlands)
    private static final Map<String, CountryInfo> DATA = Map.ofEntries(
        // UEFA (16)
        Map.entry("France",                  new CountryInfo(2,  B+"france.png",         "Frankrijk")),
        Map.entry("Spain",                   new CountryInfo(3,  B+"spain.png",           "Spanje")),
        Map.entry("England",                 new CountryInfo(5,  B+"england.png",         "Engeland")),
        Map.entry("Portugal",                new CountryInfo(6,  B+"portugal.png",        "Portugal")),
        Map.entry("Netherlands",             new CountryInfo(8,  B+"netherlands.png",     "Nederland")),
        Map.entry("Belgium",                 new CountryInfo(10, B+"belgium.png",         "België")),
        Map.entry("Germany",                 new CountryInfo(12, B+"germany.png",         "Duitsland")),
        Map.entry("Croatia",                 new CountryInfo(13, B+"croatia.png",         "Kroatië")),
        Map.entry("Switzerland",             new CountryInfo(19, B+"switzerland.png",     "Zwitserland")),
        Map.entry("Austria",                 new CountryInfo(26, B+"austria.png",         "Oostenrijk")),
        Map.entry("Norway",                  new CountryInfo(32, B+"norway.png",          "Noorwegen")),
        Map.entry("Scotland",                new CountryInfo(39, B+"scotland.png",        "Schotland")),
        Map.entry("Czech Republic",          new CountryInfo(40, B+"czech-republic.png",  "Tsjechië")),
        Map.entry("Bosnia and Herzegovina",  new CountryInfo(51, B+"bosnia.png",          "Bosnië-Herzegovina")),
        Map.entry("Turkey",                  new CountryInfo(24, B+"turkey.png",          "Turkije")),
        Map.entry("Sweden",                  new CountryInfo(35, B+"sweden.png",          "Zweden")),
        // CONMEBOL (6)
        Map.entry("Argentina",               new CountryInfo(1,  B+"argentina.png",       "Argentinië")),
        Map.entry("Brazil",                  new CountryInfo(4,  B+"brazil.png",          "Brazilië")),
        Map.entry("Colombia",                new CountryInfo(14, B+"colombia.png",        "Colombia")),
        Map.entry("Uruguay",                 new CountryInfo(17, B+"uruguay.png",         "Uruguay")),
        Map.entry("Ecuador",                 new CountryInfo(23, B+"ecuador.png",         "Ecuador")),
        Map.entry("Paraguay",                new CountryInfo(38, B+"paraguay.png",        "Paraguay")),
        // CONCACAF (6)
        Map.entry("United States",           new CountryInfo(16, B+"united-states.png",   "Verenigde Staten")),
        Map.entry("Mexico",                  new CountryInfo(15, B+"mexico.png",          "Mexico")),
        Map.entry("Canada",                  new CountryInfo(30, B+"canada.png",          "Canada")),
        Map.entry("Panama",                  new CountryInfo(33, B+"panama.png",          "Panama")),
        Map.entry("Curaçao",                 new CountryInfo(81, B+"curacao.png",         "Curaçao")),
        Map.entry("Haiti",                   new CountryInfo(83, B+"haiti.png",           "Haïti")),
        // CAF (10)
        Map.entry("Morocco",                 new CountryInfo(9,  B+"morocco.png",         "Marokko")),
        Map.entry("Senegal",                 new CountryInfo(11, B+"senegal.png",         "Senegal")),
        Map.entry("Egypt",                   new CountryInfo(29, B+"egypt.png",           "Egypte")),
        Map.entry("Ivory Coast",             new CountryInfo(27, B+"ivory-coast.png",     "Ivoorkust")),
        Map.entry("South Africa",            new CountryInfo(66, B+"south-africa.png",    "Zuid-Afrika")),
        Map.entry("Ghana",                   new CountryInfo(57, B+"ghana.png",           "Ghana")),
        Map.entry("Tunisia",                 new CountryInfo(44, B+"tunisia.png",         "Tunesië")),
        Map.entry("DR Congo",                new CountryInfo(46, B+"dr-congo.png",        "DR Congo")),
        Map.entry("Algeria",                 new CountryInfo(36, B+"algeria.png",         "Algerije")),
        Map.entry("Cape Verde",              new CountryInfo(79, B+"cape-verde.png",      "Kaapverdië")),
        // AFC (9)
        Map.entry("Japan",                   new CountryInfo(18, B+"japan.png",           "Japan")),
        Map.entry("South Korea",             new CountryInfo(22, B+"south-korea.png",     "Zuid-Korea")),
        Map.entry("Iran",                    new CountryInfo(20, B+"iran.png",            "Iran")),
        Map.entry("Australia",               new CountryInfo(25, B+"australia.png",       "Australië")),
        Map.entry("Saudi Arabia",            new CountryInfo(56, B+"saudi-arabia.png",    "Saoedi-Arabië")),
        Map.entry("Uzbekistan",              new CountryInfo(50, B+"uzbekistan.png",      "Oezbekistan")),
        Map.entry("Iraq",                    new CountryInfo(62, B+"iraq.png",            "Irak")),
        Map.entry("Jordan",                  new CountryInfo(87, B+"jordan.png",          "Jordanië")),
        Map.entry("Qatar",                   new CountryInfo(72, B+"qatar.png",           "Qatar")),
        // OFC (1)
        Map.entry("New Zealand",             new CountryInfo(100, B+"new-zealand.png",    "Nieuw-Zeeland"))
    );

    public CountryInfo get(String country) {
        return DATA.get(country);
    }

    public String getDisplayName(String country, Locale locale) {
        if (country == null) return "";
        if ("nl".equals(locale.getLanguage())) {
            CountryInfo info = DATA.get(country);
            if (info != null && info.nameNl() != null) return info.nameNl();
        }
        return country;
    }

    public List<CountryEntry> getAllSorted() {
        return DATA.entrySet().stream()
            .sorted(Comparator.comparingInt(e -> e.getValue().rank()))
            .map(e -> new CountryEntry(
                e.getKey(),
                e.getValue().rank(),
                e.getValue().logoUrl(),
                e.getValue().nameNl()))
            .collect(Collectors.toList());
    }
}
