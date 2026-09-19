-- V3__add_transcricao.sql
-- A reuniao passa a guardar os dois textos que chegam da IA:
--
--   resumo      -> o resumo executivo, curto, que vai no relatorio
--   transcricao -> a transcricao pura, longa, guardada para consulta
--
-- Separados de proposito. O relatorio consolidado mostra o resumo; a
-- transcricao fica disponivel mas nao entra no corpo do relatorio, porque e
-- texto longo e empurraria o que interessa para fora da tela.
--
-- Coluna anulavel: reuniao recem-criada ainda nao passou pela IA.

alter table reuniao add column transcricao text;

comment on column reuniao.resumo is
    'Resumo executivo vindo da IA. Entra no relatorio consolidado.';

comment on column reuniao.transcricao is
    'Transcricao pura vinda da IA. Guardada para consulta, fora do relatorio.';
