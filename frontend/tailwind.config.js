/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                // Tons customizados para o layout Dark Premium
                slate: {
                    950: '#020617',
                }
            }
        },
    },
    plugins: [],
}