import { useEffect, useState } from "react";
import axios from "axios";

export default function JobPanel() {
  const [loading, setLoading] = useState(false);
  const [jobs, setJobs] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [userId, setUserId] = useState("");
  const [message, setMessage] = useState("");
  const [editingJob, setEditingJob] = useState(null);
  const [userEmail, setUserEmail] = useState("");
  const [formData, setFormData] = useState({
    userId: userId,
    _id: "68b6f0e50f41704f213e2b0a",
    category: "IT",
    name: "Web Developer",
    location: "New Delhi",
    employmentType: "Full time",
    salary: 50000,
    description: "Red",
    responsibilities: "Something",
    requirements: "2 year of experience",
    benefits: "Vacation",
  });

  useEffect(() => {
    const storedUser = JSON.parse(localStorage.getItem("user"));
    if (storedUser) {
      setUserEmail(storedUser.email);
      setUserId(storedUser._id);
    }
  }, []);

  useEffect(() => {
    if (!userId) return; // only fetch if userId exists

    const fetchJobs = async () => {
      console.log("Sending user ID " + userId);
      try {
        const res = await axios.post(
          `${import.meta.env.VITE_API_URL}/postings/id`,
          {
            userId: userId,
          }
        );
        console.log(res.data);
        setJobs(res.data);
      } catch (err) {
        console.error(err);
      }
    };
    fetchJobs();
  }, [userId]);

  useEffect(() => {
    if (userId) {
      setFormData((prev) => ({
        ...prev,
        userId: userId,
      }));
    }
  }, [userId]);

  async function Logout() {
    const storedUser = await JSON.parse(localStorage.getItem("user"));
    console.log("User logged out with id " + storedUser);
    localStorage.removeItem("user");

    window.location.href = "/";
  }

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const resetForm = () => {
    setFormData({
      userId: formData.userId,
      _id: "",
      category: "",
      name: "",
      location: "",
      employmentType: "",
      salary: "",
      description: "",
      responsibilities: "",
      requirements: "",
      benefits: "",
    });
    setEditingJob(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (!formData.category) {
        alert("Please select a category");
        return;
      }

      if (editingJob) {
        console.log("Updating job with id:", formData._id);
        const res = await axios.put(
          `${import.meta.env.VITE_API_URL}/postings`,
          formData
        );
        setJobs(jobs.map((j) => (j._id === res.data._id ? res.data : j)));
      } else {
        console.log("Creating new job for user:", formData.userId);
        const res = await axios.post(
          `${import.meta.env.VITE_API_URL}/postings`,
          formData
        );
        setJobs([...jobs, res.data]);
      }

      resetForm();
      setShowForm(false);
    } catch (err) {
      console.error(err);
      if (err.response) console.error(err.response.data);
    }
  };

  const handleDeleteJob = async (id) => {
    try {
      await axios.delete(`${import.meta.env.VITE_API_URL}/postings`, {
        data: { _id: id },
      });
      console.log(id);
      setJobs(jobs.filter((job) => job._id !== id));
    } catch (err) {
      console.error(err);
      console.log("Error ID " + id);
      if (err.response) console.error(err.response.data);
    }
  };

  const handleEditJob = (job) => {
    setEditingJob(job);
    setFormData({
      _id: job._id,
      category: job.category,
      name: job.name,
      location: job.location,
      employmentType: job.employmentType,
      salary: job.salary,
      description: job.description,
      responsibilities: job.responsibilities,
      requirements: job.requirements,
      benefits: job.benefits,
    });
    setShowForm(true);
  };

  const SubmitForm = async (e) => {
    e.preventDefault(); // prevent page refresh

    if (!message.trim()) {
      alert("Please write a message!");
      return;
    }

    const response = await axios.post(
      `${import.meta.env.VITE_API_URL}/resend`,
      {
        user: userEmail,
        message: message,
      }
    );

    if (response.status === 200) {
      alert("Message was sent successfully");
    } else {
      alert("Problem sending a message");
    }
    // Optionally clear the textarea
    setMessage("");
  };
  return (
    <div className="flex min-h-screen">
      {/* Sidebar */}
      <aside className="w-1/4 max-h-150 mt-6 bg-gray-800 text-white p-6">
        <h2 className="text-xl font-bold mb-6">
          Give a feedback to the developer
        </h2>
        <form onSubmit={SubmitForm}>
          <textarea
            required
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            className="w-100 bg-white min-h-30 max-h-110 text-black p-3"
            placeholder="Write your message..."
          ></textarea>
          <button
            type="submit"
            className="bg-white text-black p-2 cursor-pointer hover:bg-blue-500 hover:text-white"
          >
            Send the message
          </button>
        </form>
      </aside>

      {/* Main content */}
      <div className="flex-1 p-6 max-w-6xl mx-auto">
        {/* Your existing content here */}
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold">Job Postings Panel</h1>
          <div>{userEmail}</div>
          <button
            onClick={() => {
              if (showForm && editingJob) resetForm();
              setShowForm(!showForm);
            }}
            className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 cursor-pointer"
          >
            {showForm ? "Close Form" : "Add New Job"}
          </button>
          <button
            className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 cursor-pointer"
            onClick={() => Logout()}
          >
            Logout
          </button>
        </div>

        {/* Form */}
        {showForm && (
          <form
            onSubmit={handleSubmit}
            className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6 border p-6 rounded-lg bg-gray-50"
          >
            {/* Category select */}
            <select
              name="category"
              value={formData.category}
              onChange={handleChange}
              required
              className="p-3 border rounded-lg w-full"
            >
              <option value="">Select Category</option>
              <option value="IT">IT</option>
              <option value="Finance">Finance</option>
            </select>

            {/* Short inputs in two columns */}
            {["name", "location", "employmentType", "salary"].map((field) => (
              <input
                key={field}
                type={field === "salary" ? "number" : "text"}
                name={field}
                placeholder={field.replace(/([A-Z])/g, " $1")}
                value={formData[field]}
                onChange={handleChange}
                required
                className="p-3 border rounded-lg w-full"
              />
            ))}

            {/* Description textarea with AI button below */}
            <div className="md:col-span-2 flex flex-col gap-2">
              <textarea
                name="description"
                placeholder="Description"
                value={formData.description}
                onChange={handleChange}
                required
                rows={4}
                className="p-3 border rounded-lg w-full resize-none"
              />

              <button
                type="button"
                onClick={async () => {
                  try {
                    setLoading(true); // start loading
                    const res = await axios.post(
                      `${import.meta.env.VITE_API_URL}/ai`,
                      {
                        prompt: formData.description,
                      }
                    );

                    setFormData((prev) => ({
                      ...prev,
                      responsibilities: res.data.AIresponse,
                    }));
                  } catch (error) {
                    console.error("AI API error:", error);
                  } finally {
                    setLoading(false); // stop loading
                  }
                }}
                className="self-start bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition-colors flex items-center gap-2"
                disabled={loading} // disable while loading
              >
                {loading ? (
                  <>
                    <svg
                      className="animate-spin h-5 w-5 text-white"
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                    >
                      <circle
                        className="opacity-25"
                        cx="12"
                        cy="12"
                        r="10"
                        stroke="currentColor"
                        strokeWidth="4"
                      ></circle>
                      <path
                        className="opacity-75"
                        fill="currentColor"
                        d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"
                      ></path>
                    </svg>
                    Generating...
                  </>
                ) : (
                  "Generate Responsibilities"
                )}
              </button>
            </div>

            {/* Other textareas */}
            {["responsibilities", "requirements", "benefits"].map((field) => (
              <textarea
                key={field}
                name={field}
                placeholder={field.replace(/([A-Z])/g, " $1")}
                value={formData[field]}
                onChange={handleChange}
                required
                rows={4}
                className="p-3 border rounded-lg w-full md:col-span-2 resize-none"
              />
            ))}

            {/* Submit button full row */}
            <button
              type="submit"
              className="col-span-full bg-green-500 text-white p-3 rounded-lg hover:bg-green-600 transition-colors"
            >
              {editingJob ? "Apply Changes" : "Add Job"}
            </button>
          </form>
        )}

        {/* Job listings */}
        <div className="grid gap-4">
          {jobs.length === 0 && <p>No jobs posted yet.</p>}
          {jobs.map((job) => (
            <div key={job._id} className="border rounded shadow p-4">
              <h2 className="text-xl font-semibold">{job.name}</h2>
              <p className="text-gray-600">
                {job.location} • {job.employmentType}
              </p>
              <p>💰 Salary: {job.salary}</p>
              <p>📝 Description: {job.description}</p>
              <p>📌 Responsibilities: {job.responsibilities}</p>
              <p>✅ Requirements: {job.requirements}</p>
              <p>🎁 Benefits: {job.benefits}</p>
              <p>🏷 Category: {job.category?.name || "N/A"}</p>

              <div className="mt-4 flex gap-2">
                <button
                  onClick={() => handleDeleteJob(job._id)}
                  className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
                >
                  Delete
                </button>
                <button
                  onClick={() => handleEditJob(job)}
                  className="bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600"
                >
                  Update
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
