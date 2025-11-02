// src/components/QuestionCard.jsx
export default function QuestionCard({ data, onAnswer }) {
    return (
      <div className="bg-white shadow-lg rounded-xl p-6 w-96 mx-auto">
        <h3 className="text-lg font-bold mb-4">{data.question}</h3>
        <div className="flex flex-col gap-2">
          {data.options.map((opt, i) => (
            <button
              key={i}
              onClick={() => onAnswer(opt)}
              className="border rounded-lg p-2 hover:bg-blue-200"
            >
              {opt}
            </button>
          ))}
        </div>
      </div>
    );
  }
  