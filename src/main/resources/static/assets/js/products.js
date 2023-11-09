document.getElementById("searchButton").addEventListener("click", function () {
    var searchValue = document.getElementById("searchInput").value;
    console.log(searchValue)
     searchProducts(searchValue);
});

// Function to make an AJAX request and update the product display
function searchProducts(searchValue) {
    $.ajax({
        url: "/user/products",
        type: "GET",
        data: { search: searchValue },
        success: function (data) {
             renderProducts(data);
        },
    });
}
function renderProducts(products) {
    var productsContainer = document.getElementById("products-container");
    productsContainer.innerHTML = ""; // Clear existing content

    // Iterate through the products and create and append product divs
    products.forEach(function (product) {
        // Create a div for the product
        var productDiv = document.createElement("div");
        productDiv.className = "single-popular-items mb-50 text-center";

        // Create product image element
        var productImage = document.createElement("div");
        productImage.className = "popular-img";

        var imageLink = document.createElement("a");
        imageLink.href = "/user/productDetails/" + product.id;

        var imgElement = document.createElement("img");
        imgElement.src = product.productImages[0].imageName;
        imgElement.alt = "Product Image";

        // Create other elements like the product name and price
        var captionDiv = document.createElement("div");
        captionDiv.className = "popular-caption";

        var nameElement = document.createElement("h3");
        var nameLink = document.createElement("a");
        nameLink.href = "/product/" + product.id;
        nameLink.textContent = product.productName;
        nameElement.appendChild(nameLink);

        var priceElement = document.createElement("span");
        priceElement.textContent = " $ " + product.price;

        // Append elements to their parent containers
        imageLink.appendChild(imgElement);
        productImage.appendChild(imageLink);
        productDiv.appendChild(productImage);

        captionDiv.appendChild(nameElement);
        captionDiv.appendChild(priceElement);
        productDiv.appendChild(captionDiv);

        // Append the product div to the container
        productsContainer.appendChild(productDiv);
    });
}