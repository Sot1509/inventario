export type JsonApiList<T> = {
    data: { id: string; type: string; attributes: T }[]
    meta?: any
    links?: any
}

export type JsonApiOne<T> = {
    data: { id: string; type: string; attributes: T }
}

export function unwrapList<T>(payload: JsonApiList<T>) {
    return {
        items: payload.data.map(r => ({ id: r.id, ...(r.attributes as any) })),
        meta: payload.meta,
        links: payload.links
    }
}

export function unwrapOne<T>(payload: JsonApiOne<T>) {
return { id: payload.data.id, ...(payload.data.attributes as any) }
}