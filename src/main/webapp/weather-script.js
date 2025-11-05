const apiKey = "d07d2aebee7e89f7d323407146712d2b";

var locationText = document.getElementById("location");
var dateText = document.getElementById("day-date");
var tempText = document.getElementById("temperature");
var conditionText = document.getElementById("condition");
var weatherIcon = document.getElementById("weather-icon");
var weatherSection = document.getElementById("weather");
var errorMsg = document.getElementById("error-msg");

var weatherBackgrounds = {
  Clear: "linear-gradient(to right, #fbc2eb, #a6c1ee)",
  Clouds: "linear-gradient(to right, #bdc3c7, #2c3e50)",
  Rain: "linear-gradient(to right, #00c6fb, #005bea)",
  Drizzle: "linear-gradient(to right, #89f7fe, #66a6ff)",
  Thunderstorm: "linear-gradient(to right, #373b44, #4286f4)",
  Snow: "linear-gradient(to right, #e0eafc, #cfdef3)",
  Mist: "linear-gradient(to right, #606c88, #3f4c6b)",
  Haze: "linear-gradient(to right, #f0f2f0, #000c40)",
  Fog: "linear-gradient(to right, #d7d2cc, #304352)",
  Smoke: "linear-gradient(to right, #56ccf2, #2f80ed)",
  Dust: "linear-gradient(to right, #ba8b02, #181818)",
  Ash: "linear-gradient(to right, #bdc3c7, #2c3e50)",
  Squall: "linear-gradient(to right, #1e3c72, #2a5298)",
  Tornado: "linear-gradient(to right, #4b6cb7, #182848)"
};

function formatDate() {
  var now = new Date();
  var days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
  var day = days[now.getDay()];
  var date = now.toLocaleDateString();
  return day + ", " + date;
}

function fetchWeatherByCity(city) {
  errorMsg.classList.add("hidden");
  weatherSection.classList.add("hidden");

  if (!city) {
    errorMsg.innerText = "Please enter a city name.";
    errorMsg.classList.remove("hidden");
    return;
  }

  var xhr = new XMLHttpRequest();
  var url = "https://api.openweathermap.org/data/2.5/weather?q=" + encodeURIComponent(city) + "&appid=" + apiKey + "&units=metric";

  xhr.open("GET", url, true);

  xhr.onreadystatechange = function () {
    if (xhr.readyState === 4) {
      if (xhr.status === 200) {
        var data = JSON.parse(xhr.responseText);
        var weatherMain = data.weather[0].main;
        var temp = data.main.temp;
        var cityName = data.name;
        var icon = data.weather[0].icon;
        var description = data.weather[0].description;

        locationText.innerText = cityName;
        dateText.innerText = formatDate();
        tempText.innerText = temp + " °C";
        conditionText.innerText = description;
        weatherIcon.src = "https://openweathermap.org/img/wn/" + icon + "@2x.png";
        weatherIcon.alt = description;

        weatherSection.style.background = weatherBackgrounds[weatherMain] || "linear-gradient(to right, #74ebd5, #ACB6E5)";
        weatherSection.classList.remove("hidden");
      } else {
        errorMsg.innerText = "City not found or API error.";
        errorMsg.classList.remove("hidden");
      }
    }
  };

  xhr.send();
}

document.getElementById("search-btn").onclick = function () {
  var city = document.getElementById("city-input").value.trim();
  fetchWeatherByCity(city);
};

document.getElementById("city-input").onkeypress = function (e) {
  if (e.keyCode === 13) { // Enter key
    document.getElementById("search-btn").click();
  }
};
