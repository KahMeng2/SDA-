import { useState } from "react";
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

function App() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [formValue, setFormValue] = useState("");

  const fetchData = async () => {
    setLoading(true);
    setData([]);
    try {
      const res = await fetch(`${API_BASE_URL}/test`);
      if (res.status > 299) {
        setData([`Woops! Bad response status ${res.status} from API`]);
        return;
      }
      const responseData = await res.text();
      setData(responseData.split("\n")); // Split the data by newline characters
    } catch (e) {
      setData([`Woops! An error occurred: ${e}`]);
    } finally {
      setLoading(false);
    }
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`${API_BASE_URL}/test`, {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: new URLSearchParams({
          test_value: formValue,
        }),
      });

      if (res.status === 201) {
        alert("Data submitted successfully!");
        setFormValue("");
        fetchData(); // Fetch the updated data after submission
      } else {
        alert(`Failed to submit data. Status: ${res.status}`);
      }
    } catch (e) {
      alert(`An error occurred: ${e}`);
    }
  };

  return (
    <>
      <div>
        <h3>HELLO</h3>
      </div>
      <div style={{ border: "1px solid #ccc", padding: "10px", maxWidth: "400px", margin: "10px auto" }}>
        {data.length > 0 && data.map((item, index) => (
          <div key={index} style={{ marginBottom: "5px", padding: "10px", border: "1px solid #eee", borderRadius: "5px", background: "#f9f9f9" }}>
            {item}
          </div>
        ))}
        {loading && <p>Loading...</p>}
      </div>
      <button
        disabled={loading}
        type="button"
        onClick={fetchData}
        style={{ display: "block", margin: "10px auto" }}
      >
        Fetch data
      </button>

      <form onSubmit={handleFormSubmit} style={{ maxWidth: "400px", margin: "10px auto" }}>
        <label>
          Test Value:
          <input
            type="text"
            value={formValue}
            onChange={(e) => setFormValue(e.target.value)}
            required
            style={{ display: "block", width: "100%", margin: "10px 0", padding: "8px", boxSizing: "border-box" }}
          />
        </label>
        <button type="submit" disabled={loading} style={{ display: "block", margin: "10px auto", padding: "8px 16px" }}>
          Submit
        </button>
      </form>
    </>
  );
}

export default App;
