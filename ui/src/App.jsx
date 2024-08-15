import { useState } from "react";
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

function App() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [inputValue, setInputValue] = useState("");
  const [error, setError] = useState(null);

  const fetchData = async () => {
    setLoading(true);
    setData(null);
    setError(null);
    try {
      const res = await fetch(`${API_BASE_URL}/test`);
      if (res.status > 299) {
        setData(`Woops! Bad response status ${res.status} from API`);
        return;
      }
      setData(await res.text());
    } catch (e) {
      setError(`Woops! An error occurred: ${e}`);
    } finally {
      setLoading(false);
    }
  };

  const sendData = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetch(`${API_BASE_URL}/add`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ text: inputValue }),
      });
      if (res.status > 299) {
        setError(`Woops! Bad response status ${res.status} from API`);
        return;
      }
      setData(await res.text());
    } catch (e) {
      setError(`Woops! An error occurred: ${e}`);
    } finally {
      setLoading(false);
      setInputValue(""); // Clear the input field after sending data
    }
  };

  return (
    <>
      <div>
        HELLO
      </div>
      {error && <p style={{ color: "red" }}>{error}</p>}
      {data && <p>{data}</p>}
      {loading && <p>Loading...</p>}

      <div>
        <input
          type="text"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          placeholder="Enter text to send to the database"
        />
        <button
          disabled={loading}
          type="button"
          onClick={sendData}
        >
          Send Data
        </button>
      </div>

      <button
        disabled={loading}
        type="button"
        onClick={fetchData}
      >
        Fetch Data
      </button>
    </>
  );
}

export default App;
