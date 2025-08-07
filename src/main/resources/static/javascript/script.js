document.addEventListener("click", function (event) {
    const target = event.target;

    if (target && target.id === "search-button") {
        const bandName = document.getElementById("site-field").value.trim();
        //add loading spinner
        const errorArea = document.getElementById("error-message");
        errorArea.innerHTML = '<div class="spinner">Webscraping...</div>';

        if (bandName === '') {
            errorArea.innerText = "Search field is empty";
            return;
        }

        fetch("/search-interpret?q=" + encodeURIComponent(bandName))
            .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }
                    return response.text();
            })
            .then(html => {
                const tempDiv = document.createElement("div");
                tempDiv.innerHTML = html;

                // Decide whether it's an error or quiz based on content
                if (tempDiv.querySelector(".error-text")) {
                    // It's an error fragment
                    document.getElementById("error-message").innerHTML = html;
                } else {
                    // It's a quiz form
                    document.getElementById("main-div").innerHTML = html;
                    //delete loading spinner
                    const errorArea = document.getElementById("error-message");
                    errorArea.innerHTML = '';

                    // Attach quiz form submit handler
                    const form = document.getElementById("quiz-form");
                    if (form) {
                        form.addEventListener("submit", handleFormSubmit);
                    }
                }
            })
            .catch(error => {
                    const errorArea = document.getElementById("error-message");
                    errorArea.textContent = error.message.includes("403")
                        ? "Session expired. Please refresh the page."
                        : "Failed to load quiz. Please try again.";
                    console.error("Fetch failed:", error);
            });
    }
});

function handleFormSubmit(event) {
    event.preventDefault();

    const form = event.target;
    const formData = new FormData(form);
    const jsonData = {};
    let allAnswered = true;

    // Assuming questions are numbered q1, q2, etc.
    for (let i = 1; i <= form.querySelectorAll('input[type="radio"]').length / 4; i++) {
        if (!formData.get(`q${i}`)) {
            allAnswered = false;
            break;
        }
    }

    if (!allAnswered) {
        document.getElementById("error-message").textContent = "Please answer all questions.";
        return;
    }
    document.getElementById("error-message").textContent = "";

    formData.forEach((value, key) => {
        jsonData[key] = value;
    });

    fetch("/submit-quiz", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(jsonData)
    })
    .then(response => response.text())
    .then(html => {

        const mainDiv = document.getElementById("main-div");
        mainDiv.innerHTML = html;

        //delete quiz form
        const quizForm = document.getElementById("search-area");

        //check if exist first to get rid type error
        if (quizForm != undefined) {quizForm.remove();}

        //add play again button
        const playButton = document.createElement("button");
        playButton.innerText = "Play again";

        mainDiv.appendChild(playButton);
        playButton.addEventListener("click", playButtonFunction);
    })
    .catch(err => {
        console.error("Quiz submit failed:", err);
    });
}

function playButtonFunction(){
        fetch("/new-quiz")
            .then(response => response.text())
            .then(html => {
                const mainDiv = document.getElementById("main-div");
                mainDiv.innerHTML = html; // Replace search area

            })
            .catch(error => {
                document.getElementById("error-message").textContent = "Failed to load initial stage of quiz.";
                console.error("Error loading initial stage of quiz:", error);
            });
}
