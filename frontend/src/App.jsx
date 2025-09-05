import LoginPage from "./login";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import JobPanel from "./panel";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/panel" element={<JobPanel />} />
      </Routes>
    </Router>
  );
}

export default App;
