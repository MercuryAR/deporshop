import { useState } from 'react';

interface FormValues {
  nombre: string;
  email: string;
  telefono: string;
  asunto: string;
  mensaje: string;
}

type FormErrors = Partial<Record<keyof FormValues, string>>;

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PHONE_REGEX = /^[+]?[\d\s()-]+$/;

const initialValues: FormValues = {
  nombre: '',
  email: '',
  telefono: '',
  asunto: '',
  mensaje: '',
};

function validate(values: FormValues): FormErrors {
  const errors: FormErrors = {};

  if (!values.nombre.trim()) errors.nombre = 'El nombre es obligatorio.';
  if (!values.email.trim()) {
    errors.email = 'El email es obligatorio.';
  } else if (!EMAIL_REGEX.test(values.email.trim())) {
    errors.email = 'Ingresa un email válido.';
  }
  if (values.telefono.trim() && !PHONE_REGEX.test(values.telefono.trim())) {
    errors.telefono = 'Ingresa un teléfono válido.';
  }
  if (!values.mensaje.trim()) errors.mensaje = 'El mensaje es obligatorio.';

  return errors;
}

export default function Contact() {
  const [values, setValues] = useState<FormValues>(initialValues);
  const [errors, setErrors] = useState<FormErrors>({});
  const [touched, setTouched] = useState<Partial<Record<keyof FormValues, boolean>>>({});
  const [submitted, setSubmitted] = useState(false);

  function handleChange<K extends keyof FormValues>(field: K, value: FormValues[K]) {
    const next = { ...values, [field]: value };
    setValues(next);
    if (touched[field]) {
      setErrors(validate(next));
    }
  }

  function handleBlur(field: keyof FormValues) {
    setTouched((t) => ({ ...t, [field]: true }));
    setErrors(validate(values));
  }

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    const validationErrors = validate(values);
    setErrors(validationErrors);
    setTouched({ nombre: true, email: true, telefono: true, asunto: true, mensaje: true });

    if (Object.keys(validationErrors).length === 0) {
      // No backend endpoint for contact submissions yet — simulate success like the old site did.
      console.log('Formulario de contacto enviado:', values);
      setSubmitted(true);
      setValues(initialValues);
      setTouched({});
      setErrors({});
      setTimeout(() => setSubmitted(false), 5000);
    }
  }

  function inputClass(field: keyof FormValues) {
    if (!touched[field]) return 'form-input';
    return errors[field] ? 'form-input border-error focus:border-error' : 'form-input border-success focus:border-success';
  }

  return (
    <div className="container-app py-16 max-w-2xl mx-auto">
      <h1 className="text-3xl md:text-4xl font-bold text-center mb-3">Contáctanos</h1>
      <p className="text-lg text-gray-500 text-center mb-10">
        ¿Tienes alguna pregunta? Estamos aquí para ayudarte
      </p>

      {submitted && (
        <div className="bg-gradient-to-br from-success to-emerald-400 text-white p-4 rounded-xl mb-6 shadow-lg">
          <h4 className="font-semibold mb-1">¡Mensaje enviado exitosamente!</h4>
          <p className="text-sm opacity-90">Te responderemos a la brevedad</p>
        </div>
      )}

      <form onSubmit={handleSubmit} noValidate className="bg-white p-6 md:p-8 rounded-xl shadow-lg border border-black/5 flex flex-col gap-5">
        <div className="grid sm:grid-cols-2 gap-5">
          <div>
            <label htmlFor="nombre" className="form-label">
              Nombre *
            </label>
            <input
              id="nombre"
              type="text"
              className={inputClass('nombre')}
              value={values.nombre}
              onChange={(e) => handleChange('nombre', e.target.value)}
              onBlur={() => handleBlur('nombre')}
            />
            {touched.nombre && errors.nombre && (
              <p className="text-error text-sm mt-1">{errors.nombre}</p>
            )}
          </div>

          <div>
            <label htmlFor="email" className="form-label">
              Email *
            </label>
            <input
              id="email"
              type="email"
              className={inputClass('email')}
              value={values.email}
              onChange={(e) => handleChange('email', e.target.value)}
              onBlur={() => handleBlur('email')}
            />
            {touched.email && errors.email && (
              <p className="text-error text-sm mt-1">{errors.email}</p>
            )}
          </div>
        </div>

        <div className="grid sm:grid-cols-2 gap-5">
          <div>
            <label htmlFor="telefono" className="form-label">
              Teléfono
            </label>
            <input
              id="telefono"
              type="tel"
              className={inputClass('telefono')}
              value={values.telefono}
              onChange={(e) => handleChange('telefono', e.target.value)}
              onBlur={() => handleBlur('telefono')}
            />
            {touched.telefono && errors.telefono && (
              <p className="text-error text-sm mt-1">{errors.telefono}</p>
            )}
          </div>

          <div>
            <label htmlFor="asunto" className="form-label">
              Asunto
            </label>
            <input
              id="asunto"
              type="text"
              className="form-input"
              value={values.asunto}
              onChange={(e) => handleChange('asunto', e.target.value)}
            />
          </div>
        </div>

        <div>
          <label htmlFor="mensaje" className="form-label">
            Mensaje *
          </label>
          <textarea
            id="mensaje"
            className={`${inputClass('mensaje')} min-h-[120px] resize-y`}
            value={values.mensaje}
            onChange={(e) => handleChange('mensaje', e.target.value)}
            onBlur={() => handleBlur('mensaje')}
          />
          {touched.mensaje && errors.mensaje && (
            <p className="text-error text-sm mt-1">{errors.mensaje}</p>
          )}
        </div>

        <button type="submit" className="btn-accent w-full sm:w-auto sm:self-start text-lg px-8 py-3">
          Enviar mensaje
        </button>
      </form>
    </div>
  );
}
