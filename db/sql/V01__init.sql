CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- day of week
CREATE TYPE day_of_week AS ENUM('SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY');

-- converts a postgres dow index (from extract) to our day_of_week type
CREATE OR REPLACE FUNCTIOn dow(idx numeric)
    RETURNS day_of_week LANGUAGE SQL STRICT IMMUTABLE PARALLEL SAFE AS
$$
SELECT (enum_range(null::day_of_week))[idx + 1];
$$;

-- rates table
create table rates
(
    id uuid default public.uuid_generate_v4() not null constraint rates_pk primary key,
    rate_group uuid NOT NULL,
    day day_of_week NOT NULL,
    start TIME NOT NULL,
    "end" TIME NOT NULL,
    timezone TEXT NOT NULL,
    price INTEGER NOT NULL
);

create index rates_rate_group_index on rates (rate_group);
create index rates_day_index on rates (day);
