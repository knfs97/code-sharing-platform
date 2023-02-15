const formCreateCode = document.querySelector("#create-code");
formCreateCode.addEventListener('submit', async function (e) {
    e.preventDefault();
    const formData = new FormData(this);

    const code = JSON.stringify({
        "code" : formData.get("code_snippet"),
        "time" : formData.get("time_restriction"),
        "views" : formData.get("views_restriction"),
    });
    try {
        const response = await fetch('/api/code/new', {
            method: 'POST',
            headers: {
                'Content-Type' : 'application/json; charset=utf-8'
            },
            body: code
        });

        if (response.ok) {
            alert("Success");
        }
    } catch (error) {
        console.log(error.message);
    }

})