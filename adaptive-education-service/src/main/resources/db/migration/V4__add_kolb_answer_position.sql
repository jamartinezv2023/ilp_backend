-- Preserve the original order of newly persisted Kolb responses.
-- Historical positions cannot be inferred from unordered answer values.
-- Abort rather than fabricate positions or modify historical responses.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM public.kolb_assessment_answers
    ) THEN
        RAISE EXCEPTION
            'Kolb answer-position migration blocked: existing responses require a separately reviewed preservation plan';
    END IF;
END
$$;
ALTER TABLE public.kolb_assessment_answers
    ADD COLUMN answer_position integer;
ALTER TABLE public.kolb_assessment_answers
    ADD CONSTRAINT ck_kolb_answer_position_nonnegative
    CHECK (answer_position >= 0);
