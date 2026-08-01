import { useCallback, useEffect, useRef, useState } from 'react';

export interface CarouselSlide {
  id: number | string;
  image: string;
  title: string;
  subtitle?: string;
  linkTo?: string;
}

interface CarouselProps {
  slides: CarouselSlide[];
  autoPlayMs?: number;
}

export default function Carousel({ slides, autoPlayMs = 4000 }: CarouselProps) {
  const [index, setIndex] = useState(0);
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const goTo = useCallback(
    (i: number) => {
      setIndex(((i % slides.length) + slides.length) % slides.length);
    },
    [slides.length],
  );

  const next = useCallback(() => goTo(index + 1), [goTo, index]);
  const prev = useCallback(() => goTo(index - 1), [goTo, index]);

  useEffect(() => {
    if (!autoPlayMs || slides.length <= 1) return;
    timerRef.current = setInterval(() => {
      setIndex((i) => (i + 1) % slides.length);
    }, autoPlayMs);
    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, [autoPlayMs, slides.length]);

  if (slides.length === 0) return null;

  return (
    <div className="relative w-full max-w-[650px] mx-auto rounded-xl shadow-xl overflow-hidden border border-black/5 bg-white">
      <div
        className="flex transition-transform duration-500 ease-in-out"
        style={{ transform: `translateX(-${index * 100}%)` }}
      >
        {slides.map((slide) => (
          <div key={slide.id} className="min-w-full relative aspect-video">
            <img src={slide.image} alt={slide.title} className="w-full h-full object-cover" />
            <div className="absolute inset-0 bg-gradient-to-t from-black/70 to-transparent pointer-events-none" />
            <div className="absolute bottom-0 left-0 right-0 p-6 text-white">
              <h3 className="text-xl font-bold">{slide.title}</h3>
              {slide.subtitle && <p className="opacity-90">{slide.subtitle}</p>}
            </div>
          </div>
        ))}
      </div>

      {slides.length > 1 && (
        <>
          <button
            aria-label="Anterior"
            onClick={prev}
            className="absolute top-1/2 -translate-y-1/2 left-4 w-10 h-10 rounded-full bg-white shadow-md flex items-center justify-center hover:bg-primary hover:text-white transition-all z-10"
          >
            ‹
          </button>
          <button
            aria-label="Siguiente"
            onClick={next}
            className="absolute top-1/2 -translate-y-1/2 right-4 w-10 h-10 rounded-full bg-white shadow-md flex items-center justify-center hover:bg-primary hover:text-white transition-all z-10"
          >
            ›
          </button>
          <div className="absolute bottom-4 left-1/2 -translate-x-1/2 flex gap-1.5 bg-black/30 backdrop-blur px-3 py-1.5 rounded-full z-10">
            {slides.map((slide, i) => (
              <button
                key={slide.id}
                aria-label={`Ir a la diapositiva ${i + 1}`}
                onClick={() => goTo(i)}
                className={`h-2 rounded-full transition-all border-2 border-transparent ${
                  i === index ? 'w-5 bg-white border-primary' : 'w-2 bg-white/50 hover:bg-white/80'
                }`}
              />
            ))}
          </div>
        </>
      )}
    </div>
  );
}
