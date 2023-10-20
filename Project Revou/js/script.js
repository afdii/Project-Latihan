document.getElementById("button").addEventListener("click", function() {
    let name = document.forms['contact-form'] ['your-name'].value;
    let email = document.forms['contact-form'] ['email'].value;
    let interest = document.forms['contact-form'] ['interest'].value;

    if (!name) {
        document.getElementById("name-error").innerText = "Name cannot be blank!";
    } else {
        document.getElementById("name-error").innerText =""
    }

    if (!email) {
        document.getElementById("email-error").innerText ="Email cannot be blank!";
    } else {
        document.getElementById("email-error").innerText = ""
    }

    if (!interest) {
        document.getElementById("interest-error").innerText ="Interest cannot be blank!";
    } else {
        document.getElementById("interest-error").innerText  =""
    }

});

const carouselSlide = document.querySelectorAll(".image");
let currentIndex = 0;
let interval = 3000;

function nextSlide() {
    carouselSlide[currentIndex].classList.remove("active");
    currentIndex = (currentIndex + 1) % carouselSlide.length;
    carouselSlide[currentIndex].classList.add('active')
}

function starSlide() {
    setInterval(nextSlide, interval);
}

(function() {
    starSlide();
})()
