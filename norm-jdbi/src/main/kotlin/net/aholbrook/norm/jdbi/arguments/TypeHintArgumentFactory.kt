package net.aholbrook.norm.jdbi.arguments

import net.aholbrook.norm.util.TypeHint
import org.jdbi.v3.core.argument.AbstractArgumentFactory
import org.jdbi.v3.core.argument.Argument
import org.jdbi.v3.core.config.ConfigRegistry
import java.sql.Types

object TypeHintArgumentFactory : AbstractArgumentFactory<TypeHint>(Types.OTHER) {
    override fun build(value: TypeHint, config: ConfigRegistry?): Argument = Argument { position, statement, _ ->
        statement?.setObject(position, value.value, value.type)
    }
}
